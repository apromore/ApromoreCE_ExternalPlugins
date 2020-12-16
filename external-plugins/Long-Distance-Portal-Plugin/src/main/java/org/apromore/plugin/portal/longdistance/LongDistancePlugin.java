/*
 * Copyright © 2020 The University of Melbourne.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.longdistance;


import java.util.*;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.apromore.model.SummaryType;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zul.*;
import org.apache.commons.math3.util.Pair;

// Local packages

import org.apromore.helper.Version;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.custom.gui.plugin.PluginCustomGui;
import org.apromore.service.ProcessService;
import org.apromore.service.bpmndiagramimporter.BPMNDiagramImporter;
import org.apromore.model.LogSummaryType;
import dk.brics.automaton.*;


/**
 * Plugin for the discovery of non-local dependencies. Created by Anna Kalenkova 28/01/2020
 */

@Component("plugin")
public class LongDistancePlugin extends PluginCustomGui {
    
	enum Type 
	{ 
	    ENTER, EXIT, DO_NOT_CROSS; 
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LongDistancePlugin.class);
	private static final Set<Set<State>> allRegions = new HashSet<Set<State>>();


    @Inject private ProcessService processService;
    @Inject private BPMNDiagramImporter importerService;
    @Inject private EventLogService eventLogService; 
    
    private Map<String, Character> mapOfSymbols = new HashMap<String, Character>();      
    
    @Override
    public String getLabel(Locale locale) {
        return "Add long-distance relations";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Discover";
    }

    @Override
    public void execute(final PortalContext context) {
    
    	
    	  // Show a message on the portal
        try {
            Set<SummaryType> elements = context.getSelection().getSelectedProcessModels();
            Map<SummaryType,List<VersionSummaryType>> vsts = context.getSelection().getSelectedProcessModelVersions();
            Set<ProcessSummaryType> selectedProcessess = new HashSet<>();
            Set<LogSummaryType> selectedLogs = new HashSet<>();
          
            
            for(SummaryType entry : elements) {
                if(entry instanceof ProcessSummaryType) {
                	selectedProcessess.add((ProcessSummaryType)entry);
                }
                if(entry instanceof LogSummaryType) {
                	selectedLogs.add((LogSummaryType)entry);
                }
            }

            // 1 process model must be selected. 
            if (selectedProcessess.size() != 1) {
                Messagebox.show("Select one process model for the enhancement.");
                return;
            }
            
            // 1 log must be selected. 
            if (selectedLogs.size() != 1) {
                Messagebox.show("Select one event log.");
                return;
            }
            
            ProcessSummaryType process = selectedProcessess.iterator().next();
            VersionSummaryType vst = vsts.get(process).get(0);

            // Fetch the BPMN serialization of the model
            int procID = process.getId();
            String procName = process.getName();
            String branch = vst.getName();
            Version version = new Version(vst.getVersionNumber());
//            try {
//            	Messagebox.show("Getting bpmn representation...");
                String bpmn = processService.getBPMNRepresentation(procName, procID, branch, version);
                BPMNDiagram bpmnDiagram = importerService.importBPMNDiagram(bpmn);
//              Messagebox.show("Finding free-choice candidates...");
                Set<Set<Activity>> freeChoiceCandidates = findFreeChoiceCandidates(bpmnDiagram);
                
                
                
                LogSummaryType logType = selectedLogs.iterator().next();
                XLog log = eventLogService.getXLog(logType.getId());
                
//                Messagebox.show("Log size " + log.size());
//                LOGGER.info("Log size " + log.size());

                
//              Messagebox.show("Building automaton...");
                Automaton aL = buildAutomaton(log);
                aL.minimize();
//              aL.determinize();
//              Messagebox.show(aL.toString());
//              LOGGER.info(aL.toString());
//              Messagebox.show(mapOfSymbols.toString());
                LOGGER.info(mapOfSymbols.toString());
               
                long time = System.currentTimeMillis();
                // Check free choice
                for(Set<Activity> freeChoicecandidate : freeChoiceCandidates) {
                	
                	if(!checkFreechoice(freeChoicecandidate, aL)) {
//                		 Messagebox.show(freeChoicecandidate + " non free choice");
                		 LOGGER.info(freeChoicecandidate + " non free choice");
                		 for(Activity a : freeChoicecandidate) {
                			 LOGGER.info("Finding regions for activity " + a);
                			 findMinimalRegions(a, freeChoicecandidate, aL, bpmnDiagram);
                		 }
                	}
                }
                System.out.println("Overall time: " + (System.currentTimeMillis() - time));
//                Messagebox.show("Calculation time " + (System.currentTimeMillis() - time));
//           	} 
//            } catch (Exception e) {
//                Messagebox.show("Unable to read " + procName, "Attention", Messagebox.OK, Messagebox.ERROR);
//                e.printStackTrace();
//            }
        } catch (Exception e) {
            e.printStackTrace();
//        	Messagebox.show("Unable discover long distance dependancies: " + e, "Error", 0, Messagebox.ERROR);
            LOGGER.error("Unable discover long distance dependancies", e);
        }
    }
        
    private void findMinimalRegions(Activity a, Set<Activity> freeChoiceCandidates, Automaton aL, BPMNDiagram bpmn) {
    	
    	Set<State> region = new HashSet<State>();
    	
    	Set<Character> freeChoiceSymb = new HashSet<Character>();
    	for(Activity a1 : freeChoiceCandidates) {
    		freeChoiceSymb.add(mapOfSymbols.get(a1.getLabel()));
    	}
    	int numberOfStates = 0;
    	
    	for(State s : aL.getStates()) {
    		for(Transition t : s.getTransitions()) {
    			char maxChar = t.getMax();
    			char minChar = t.getMin();
    			if ((freeChoiceSymb.contains(maxChar)) || (freeChoiceSymb.contains(minChar))) {
    				numberOfStates++;
    			}
    			if(mapOfSymbols.get(a.getLabel()) != null) {
    				char hashCode = mapOfSymbols.get(a.getLabel());
    				if ((hashCode <= maxChar) && (hashCode >= minChar)) {
    					region.add(s);
    				}
				}
    		}
    	}
    	
    	if(region.size() < numberOfStates) {
//    		Messagebox.show(region.toString());
    		expandAndAddRegionsOpt(a, region, aL);    		
    	}
    	
    }
    
	private Set<Pair<State,Transition>> findIncomingTransitions(Set<State> region, Automaton aL) {
		Set<Pair<State,Transition>> result = new HashSet<Pair<State,Transition>>();

		for (State s : aL.getStates()) {
			for (Transition t : s.getTransitions()) {
				if (region.contains(t.getDest()) && !region.contains(s)) {
					result.add(new Pair<State,Transition>(s,t));

				}
			}
		}

		return result;
	}
	
    
	private  Set<Pair<State,Transition>> findOutgoingTransitions(Set<State> region, Automaton aL) {
		
		Set<Pair<State,Transition>> result = new HashSet<Pair<State,Transition>>();

		for (State s : aL.getStates()) {
			for (Transition t : s.getTransitions()) {
				if (!region.contains(t.getDest()) && region.contains(s)) {
					result.add(new Pair<State,Transition>(s,t));

				}
			}
		}

		return result;
	}
    
    private Set<Pair<State,Transition>> findTransitionsLabeledBySymbol(char c, Automaton aL) {
    	Set<Pair<State,Transition>> result = new HashSet<Pair<State,Transition>>();
    	
    	for(State s : aL.getStates()) {
        	for (Transition t : s.getTransitions()) {
        		char symb = t.getMin();
				if (symb == c) {
					result.add(new Pair<State,Transition>(s,t));
				}
        	}
    	}
    	
    	return result;
    }
    
    private void expandAndAddRegionsOpt(Activity outActivity, Set<State> region, Automaton aL) {
    	
    	String finalRegions = "";
    	LOGGER.info("Expanding region of the size: " + region.size());
    	Character outSymbol = mapOfSymbols.get(outActivity.getLabel());

    	Map<Character, Type> typeOfActivity= new HashMap<Character, Type>();
    	Set<Pair<State,Transition>> incomingTransitions =  findIncomingTransitions(region, aL);
    	
    	Set<Character> analyzedInputSymbols = new HashSet<Character>();
    	
		for (Pair<State,Transition> incomingTransition : incomingTransitions) {
			char c = incomingTransition.getSecond().getMin();
			LOGGER.info("Analysing incoming transitions labeled by " + stringFromChar(c));
			if (analyzedInputSymbols.contains(new Character(c))) {
				continue;
			}
			analyzedInputSymbols.add(new Character(c));
			
			Set<Pair<State, Transition>> labeledTransitions = findTransitionsLabeledBySymbol(c, aL);
			for (Pair<State, Transition> labeledTransition : labeledTransitions) {

				// IF ENTERS
				if ((!region.contains(labeledTransition.getFirst()))
						&& region.contains(labeledTransition.getSecond().getDest())) {
					// Do nothing, all good
					LOGGER.info("ENTERS");
				}

				// IF EXITS
				if ((region.contains(labeledTransition.getFirst()))
						&& !region.contains(labeledTransition.getSecond().getDest())) {

					LOGGER.info("EXITS");
					//Adding entering/exiting transitions labeled by c inside				
					Set<Pair<State, Transition>> labeledTransitions2 = findTransitionsLabeledBySymbol(c, aL);
					for(Pair<State, Transition> labeledTransition2 : labeledTransitions2) {
						if ((region.contains(labeledTransition2.getFirst()))
								|| region.contains(labeledTransition2.getSecond().getDest())) {
							region.add(labeledTransition2.getFirst());
							region.add(labeledTransition2.getSecond().getDest());
						}
					}
					if(!allRegions.contains(region)) {
						allRegions.add(region);
						expandAndAddRegionsOpt(outActivity, region, aL);
					}
					return;
				}
				
				// IF DO NOT CROSS (inside)
				if ((region.contains(labeledTransition.getFirst()))
						&& region.contains(labeledTransition.getSecond().getDest())) {
					
					LOGGER.info("INSIDE");
					//Adding entering/exiting transitions labeled by c inside
					Set<Pair<State, Transition>> labeledTransitions2 = findTransitionsLabeledBySymbol(c, aL);
					for(Pair<State, Transition> labeledTransition2 : labeledTransitions2) {
						if ((region.contains(labeledTransition2.getFirst()))
								|| region.contains(labeledTransition2.getSecond().getDest())) {
							region.add(labeledTransition2.getFirst());
							region.add(labeledTransition2.getSecond().getDest());
						}
					}
					if(!allRegions.contains(region)) {
						allRegions.add(region);
						expandAndAddRegionsOpt(outActivity, region, aL);
					}
					return;
				}
				
//				// Check that EXIT or INSIDE exist
//				boolean existsEnterOrOutside = false;
//				Set<Pair<State, Transition>> labeledTransitions2 = findTransitionsLabeledBySymbol(c, aL);
//				for(Pair<State, Transition> labeledTransition2 : labeledTransitions2) {
//					if (region.contains(labeledTransition2.getFirst())) {
//						existsEnterOrOutside = true;
//					}
//				}
//				
//				if(existsEnterOrOutside) {
//					continue;
//				}	
				
			}
			
			labeledTransitions = findTransitionsLabeledBySymbol(c, aL);
			for (Pair<State, Transition> labeledTransition : labeledTransitions) {

				// IF DO NOT CROSS (outside)
				if ((!region.contains(labeledTransition.getFirst()))
						&& !region.contains(labeledTransition.getSecond().getDest())) {
					
					LOGGER.info("OUTSIDE");
					Set<Pair<State, Transition>> labeledTransitions2 = findTransitionsLabeledBySymbol(c, aL);
					
					// Making DO NOT CROSS
					LOGGER.info("MAKING DO NOT CROSS");
					Set<State> newRegion = new HashSet<State>();
					newRegion.addAll(region);
					for (Pair<State, Transition> labeledTransition2 : labeledTransitions2) {
						if (region.contains(labeledTransition2.getSecond().getDest())) {
							newRegion.add(labeledTransition2.getFirst());
						}
					}
					if (!allRegions.contains(newRegion)) {
						allRegions.add(newRegion);
						expandAndAddRegionsOpt(outActivity, newRegion, aL);
					}

					// Making ENTER
					LOGGER.info("MAKING ENTER");
					boolean changed = false;
					for (Pair<State, Transition> labeledTransition2 : labeledTransitions2) {
						if(!region.contains(labeledTransition2.getSecond().getDest())) {
							region.add(labeledTransition2.getSecond().getDest());
							changed = true;
						}
					}
					if(changed) {
						expandAndAddRegionsOpt(outActivity, region, aL);
						return;
					}
				}
			}
		}
			
		Set<Character> analyzedOutputSymbols = new HashSet<Character>();
		Set<Pair<State,Transition>> outgoingTransitions =  findOutgoingTransitions(region, aL);
		for (Pair<State,Transition> outgoingTransition : outgoingTransitions) {
			char c = outgoingTransition.getSecond().getMin();
			
			LOGGER.info("Analysing outgoing transitions labeled by " + stringFromChar(c));
			if (analyzedOutputSymbols.contains(new Character(c))) {
				continue;
			}
			analyzedOutputSymbols.add(new Character(c));
			
			Set<Pair<State, Transition>> labeledTransitions = findTransitionsLabeledBySymbol(c, aL);
			for (Pair<State, Transition> labeledTransition : labeledTransitions) {

				// IF EXITS
				if ((region.contains(labeledTransition.getFirst()))
						&& !region.contains(labeledTransition.getSecond().getDest())) {
					// Do nothing, all good
				}

				// IF ENTERS
				if ((!region.contains(labeledTransition.getFirst()))
						&& region.contains(labeledTransition.getSecond().getDest())) {

					
					//Adding entering/exiting transitions labeled by c inside
					Set<Pair<State, Transition>> labeledTransitions2 = findTransitionsLabeledBySymbol(c, aL);
					for(Pair<State, Transition> labeledTransition2 : labeledTransitions2) {
						if ((region.contains(labeledTransition2.getFirst()))
								|| region.contains(labeledTransition2.getSecond().getDest())) {
							region.add(labeledTransition2.getFirst());
							region.add(labeledTransition2.getSecond().getDest());
						}
					}
					if(!allRegions.contains(region)) {
						allRegions.add(region);
						expandAndAddRegionsOpt(outActivity, region, aL);
					}
					return;
				}
				
				// IF DO NOT CROSS (inside)
				if ((region.contains(labeledTransition.getFirst()))
						&& region.contains(labeledTransition.getSecond().getDest())) {
					
					/* OPTIMIZATION : If OCCASIONALLY outActivity is inside, then stop and return*/
					if(outSymbol.equals(new Character(c))) {
						return;
					}
					
					//Adding entering/exiting transitions labeled by c inside
					Set<Pair<State, Transition>> labeledTransitions2 = findTransitionsLabeledBySymbol(c, aL);
					for(Pair<State, Transition> labeledTransition2 : labeledTransitions2) {
						if ((region.contains(labeledTransition2.getFirst()))
								|| region.contains(labeledTransition2.getSecond().getDest())) {
							region.add(labeledTransition2.getFirst());
							region.add(labeledTransition2.getSecond().getDest());
						}
					}
					if(!allRegions.contains(region)) {
						allRegions.add(region);
						expandAndAddRegionsOpt(outActivity, region, aL);
					}
					return;
				
				}
				
//				// Check that either EXIT or OUTSIDE exist
//				boolean existsExitOrOutside = false;
//				Set<Pair<State, Transition>> labeledTransitions2 = findTransitionsLabeledBySymbol(c, aL);
//				for(Pair<State, Transition> labeledTransition2 : labeledTransitions2) {
//					if (!region.contains(labeledTransition2.getSecond().getDest())) {
//						existsExitOrOutside = true;
//					}
//				}
//				
//				if(existsExitOrOutside) {
//					continue;
//				}
			}
			
			labeledTransitions = findTransitionsLabeledBySymbol(c, aL);
			for (Pair<State, Transition> labeledTransition : labeledTransitions) {
					
				// IF DO NOT CROSS (outside)
				if ((!region.contains(labeledTransition.getFirst()))
						&& !region.contains(labeledTransition.getSecond().getDest())) {		
					Set<Pair<State, Transition>> labeledTransitions2 = findTransitionsLabeledBySymbol(c, aL);
					// Making DO NOT CROSS
					Set<State> newRegion = new HashSet<State>();
					newRegion.addAll(region);
					for(Pair<State, Transition> labeledTransition2 : labeledTransitions2) {
						if (region.contains(labeledTransition2.getFirst())) {
							newRegion.add(labeledTransition2.getSecond().getDest());
						}
					}
					if(!allRegions.contains(newRegion)) {
						allRegions.add(newRegion);
						expandAndAddRegionsOpt(outActivity, newRegion, aL);
					}
					
					
					// Making EXIT
					boolean changed = false;
					for(Pair<State, Transition> labeledTransition2 : labeledTransitions2) {
						if(!region.contains(labeledTransition2.getFirst())) {
							region.add(labeledTransition2.getFirst());
							changed = true;
						}
					}
					if(changed) {
						expandAndAddRegionsOpt(outActivity, region, aL);
						return;
					}		
				}
			}
		}
    	LOGGER.info("New region found " + region);
    	finalRegions += "New region: " + region + "\n";
    	//Messagebox.show("New region found: " + region);
    	incomingTransitions =  findIncomingTransitions(region, aL);
    	String in = "";
    	Set<String> inStrings = new HashSet<String>();
    	for(Pair<State,Transition> t : incomingTransitions) {
    		char c =  t.getSecond().getMax();
    		String inString = stringFromChar(c);
    		if(!inStrings.contains(inString)) {
    			in += inString + ";";
    			inStrings.add(inString);
    		}
    			
    	}
    	LOGGER.info("In: " + in);
    	outgoingTransitions = findOutgoingTransitions(region, aL);
    	String out = "";
    	Set<String> outStrings = new HashSet<String>();
    	for(Pair<State,Transition> t : outgoingTransitions) {
    		char c =  t.getSecond().getMax();
    		String outString = stringFromChar(c);
    		if(!outStrings.contains(outString)) {
    			out += outString + ";";
    			outStrings.add(outString);
    		}
    	}
    	LOGGER.info("Out: " + out);
    	
    	Messagebox.show(finalRegions);
    	return;
    }
    
    private String stringFromChar(char c) {
    	String result = "";
    	
    	for (String s : mapOfSymbols.keySet()) {
    		if(mapOfSymbols.get(s) == c) {
    			result = s;
    		}
    	}
    	
    	return result;
    	
    }
    
    private void expandAndAddRegions(Activity outActivity, Set<Activity> freeChoiceCandidates, Set<State> region, Automaton aL) {
    	
//    	Messagebox.show("Activity  " + outActivity.getLabel());
//    	Messagebox.show("Expanding region " + region);
    	LOGGER.info("Expanding region " + region);
    	Map<Character, Type> typeOfActivity= new HashMap<Character, Type>();
    	Character outSymbol = mapOfSymbols.get(outActivity.getLabel());
    	
    	Set<Character> freeChoiceSymb = new HashSet<Character>();
    	for(Activity a : freeChoiceCandidates) {
    		freeChoiceSymb.add(mapOfSymbols.get(a.getLabel()));
    	}
    	
    	Set<State> outStates = new HashSet<State>();
    	for(State s : aL.getStates()) {
    		for (Transition t : s.getTransitions()) {
    			char maxChar = t.getMax();
    			char minChar = t.getMin();
    			if ((outSymbol >= minChar) && (outSymbol <= maxChar)) {
    				outStates.add(t.getDest());
    			}
    		}
    	}
    	
    	for(State s : aL.getStates()) {
    		for (Transition t : s.getTransitions()) {
    			char maxChar = t.getMax();
    			char minChar = t.getMin();
    			if(!region.contains(s)) {
	    			if ((freeChoiceSymb.contains(maxChar)) && freeChoiceSymb.contains(minChar)) {
	    				outStates.add(s);
	    			}
    			}
    		}
    	}
    	
    	
    	Set<Character> possibleOutgoingCharaters = new HashSet<Character>();
    	for(Activity possibleOutActivity: freeChoiceCandidates) {
    		possibleOutgoingCharaters.add(mapOfSymbols.get(possibleOutActivity.getLabel()));
    	}
    	
//    	Messagebox.show("Out states " + outStates);
    	
    	for(State s : aL.getStates()) {
    		LOGGER.info("Type of activity " + typeOfActivity);
    		for (Transition t : s.getTransitions()) {
    			char maxChar = t.getMax();
    			char minChar = t.getMin();
    			for(char c = minChar; c <= maxChar; c++) {
    				
    				// IF EXIT
    				if ((region.contains(s)) && !region.contains(t.getDest())) {
    					if (typeOfActivity.get(new Character(c)) == null) {
    						typeOfActivity.put(new Character(c), Type.EXIT);
    					} else {
    						if(typeOfActivity.get(new Character(c)).equals(Type.ENTER)) {
    							if(!outStates.contains(t.getDest())) {
    								region.add(t.getDest());  
    								expandAndAddRegions(outActivity, freeChoiceCandidates, region, aL);
    							}
    							return;
    						}
    						if (typeOfActivity.get(new Character(c)).equals(Type.DO_NOT_CROSS)) {
    							if(!outStates.contains(t.getDest())) {
    								Set<State> region1 = new HashSet<State>();
    								region1.addAll(region);
    								region1.add(t.getDest());  
    								expandAndAddRegions(outActivity, freeChoiceCandidates, region1, aL);
    							}
    							
    							if((possibleOutgoingCharaters.contains(c))) {
	    							Set<State> region2 = new HashSet<State>();
	    							region2.addAll(region);
	    							for(State s1 : aL.getStates()) {
	    								for (Transition t1 : s1.getTransitions()) {
	    									char maxChar1 = t1.getMax();
	    					    			char minChar1 = t1.getMin();
	    					    			if ((c>=minChar1) && (c<=maxChar1)) {
	    					    				if(!outStates.contains(s1)) {
	    					    					region2.add(s1);
	    					    				}
	    					    			}
	    								}
	    							}
	    							if(region2.size() > region.size()) {
	    								expandAndAddRegions(outActivity, freeChoiceCandidates, region2, aL);
	    							}
	    							return;
    							}
    							
    						}
    					}
    				}
    				// IF ENTER
    				else if ((!region.contains(s)) && region.contains(t.getDest())) {
    					if (typeOfActivity.get(new Character(c)) == null) {
    						typeOfActivity.put(new Character(c), Type.ENTER);
    					} else {
    						if(typeOfActivity.get(new Character(c)).equals(Type.EXIT)) {
    							if(!outStates.contains(s)) {
    								region.add(s);  
    								expandAndAddRegions(outActivity, freeChoiceCandidates, region, aL);
    							}
    							return;
    						}
    						if(typeOfActivity.get(new Character(c)).equals(Type.DO_NOT_CROSS)) {
    							if(!outStates.contains(s)) {
	    							Set<State> region1 = new HashSet<State>();
	    							region1.addAll(region);
	    							region1.add(s);  
	    							expandAndAddRegions(outActivity, freeChoiceCandidates, region1, aL);
    							}
    							
    							Set<State> region2 = new HashSet<State>();
    							region2.addAll(region);
    							for(State s1 : aL.getStates()) {
    								for (Transition t1 : s1.getTransitions()) {
    									char maxChar1 = t1.getMax();
    					    			char minChar1 = t1.getMin();
    					    			if(!outStates.contains(t1.getDest())) {
	    					    			if ((c>=minChar1) && (c<=maxChar1)) {
	    					    				region2.add(t1.getDest());
	    					    			}
    					    			}
    								}
    							}
    							if(region2.size() > region.size()) {
    								expandAndAddRegions(outActivity, freeChoiceCandidates, region2, aL);
    							}
    							return;
    						}
    					}
    				}
    				// IF DO NOT CROSS
    				else {
    					if (typeOfActivity.get(new Character(c)) == null) {
    						typeOfActivity.put(new Character(c), Type.DO_NOT_CROSS);
    					} else {
    						if(typeOfActivity.get(new Character(c)).equals(Type.EXIT)) {
    							
    							if((possibleOutgoingCharaters.contains(c))) {
	    							if(!region.contains(s) && !outStates.contains(s)) {
	    								Set<State> region1 = new HashSet<State>();
		    							region1.addAll(region);
		    							region1.add(s); 
		    							expandAndAddRegions(outActivity, freeChoiceCandidates, region1, aL);
	    							}
    							}
	    							
    							Set<State> region2 = new HashSet<State>();
    							region2.addAll(region);
    							for(State s1 : aL.getStates()) {
    								for (Transition t1 : s1.getTransitions()) {
    									char maxChar1 = t1.getMax();
    					    			char minChar1 = t1.getMin();
    					    			if ((c>=minChar1) && (c<=maxChar1)) {
    					    				if(!outStates.contains(t1.getDest())) {
    					    					if(region.contains(s1)) { 
    					    						region2.add(t1.getDest());
    					    					}
    					    				}
    					    			}
    								}
    							}
    							if(region2.size() > region.size()) {
    								expandAndAddRegions(outActivity, freeChoiceCandidates, region2, aL);
    							}
    							return;
    						}
    						if(typeOfActivity.get(new Character(c)).equals(Type.ENTER)) {
    							
    							
    							if(!region.contains(t.getDest()) && !outStates.contains(t.getDest())) {
	    							Set<State> region1 = new HashSet<State>();
	    							region1.addAll(region);
	    							region1.add(t.getDest()); 
	    							expandAndAddRegions(outActivity, freeChoiceCandidates, region1, aL);
    							}
    							
    							Set<State> region2 = new HashSet<State>();
    							region2.addAll(region);
    							for(State s1 : aL.getStates()) {
    								for (Transition t1 : s1.getTransitions()) {
    									char maxChar1 = t1.getMax();
    					    			char minChar1 = t1.getMin();
    					    			if ((c>=minChar1) && (c<=maxChar1)) {
    					    				if(!outStates.contains(s1)) {
    					    					if(region.contains(t1.getDest())) { 
    					    						region2.add(s1);
    					    					}
    					    				}
    					    			}
    							
    								}
    							}
    							if(region2.size() > region.size()) {
    								expandAndAddRegions(outActivity, freeChoiceCandidates, region2, aL);
    							}
    							return;
    						}
    					}
    				}
    			}
    		}
    	}
    	
    	//Messagebox.show("New region found: " + typeOfActivity);
    	LOGGER.info("New region found " + region);
    	return;
    }
  
    private boolean checkFreechoice(Set<Activity> freeChoicecandidate, Automaton aL) {
    	
    	for(State s : aL.getStates()) {
    		int outActivity = 0;
    		for(Transition t : s.getTransitions()) {
    			char maxChar = t.getMax();
    			char minChar = t.getMin();
    			for(Activity a: freeChoicecandidate) {
//    				Messagebox.show(a.getLabel());
//    				Messagebox.show(mapOfSymbols.get(a.getLabel()).toString());
//    				Messagebox.show("Max char" + maxChar);
//    				Messagebox.show("Min char" + minChar);
    				if(mapOfSymbols.get(a.getLabel()) != null) {
	    				char hashCode = mapOfSymbols.get(a.getLabel());
	    				if ((hashCode <= maxChar) && (hashCode >= minChar)) {
	    					outActivity ++;
	    				}
    				}
    			}
    		}
    		if ((outActivity > 0) && (outActivity < freeChoicecandidate.size())) {
    			return false;
    		}
    	}
    	
    	return true;
    	
    }
        /**
    	 * Build a transition system from log using window and prefixes
    	 * 
    	 */
	@SuppressWarnings("deprecation")
	private Automaton buildAutomaton(XLog log) {

		Set<String> allTraces = new HashSet<String>();
		Automaton a = new Automaton();
		a.setInitialState(new State());

		// For each trace define states and transitions
		for (XTrace trace : log) {
			String sTrace = "";
			for (XEvent e : trace) {
				String sEvent = e.getAttributes().get("concept:name").toString();
				int hashCode = sEvent.hashCode();
				char symb = (char) hashCode;
				if (mapOfSymbols.get(sEvent) == null) {
					mapOfSymbols.put(sEvent, new Character((char) hashCode));
				}
				sTrace += symb;
			}
			if(!allTraces.contains(sTrace)) {
				Automaton temp = Automaton.makeString(sTrace);
				a = a.union(temp);
			
				allTraces.add(sTrace);
			}
		}
		return a;
	}

    private Set<Set<Activity>> findFreeChoiceCandidates(BPMNDiagram bpmnDiagram) {
    	Set<Set<Activity>> freechoiceCandidates = new HashSet<Set<Activity>>();
    	for(Gateway g : bpmnDiagram.getGateways()) {
    		if(g.getGatewayType().equals(GatewayType.DATABASED)) {
    			if(bpmnDiagram.getOutEdges(g).size() > 1) {
    				Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> outEdges = bpmnDiagram.getOutEdges(g);
    				Set<Activity> activitiesInFreeChoice = new HashSet<Activity>();
    				for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e : outEdges) {
    					BPMNNode outNode = e.getTarget();
    					if(outNode instanceof Activity) {
    						activitiesInFreeChoice.add((Activity)(outNode));
    					}
    				}
					//if (activitiesInFreeChoice.size() > 1) {
						freechoiceCandidates.add(activitiesInFreeChoice);
					//}
    			}
    		}
    	}
    	
//    	Messagebox.show(freechoiceCandidates.toString());
    	return freechoiceCandidates;
    }
    
}
