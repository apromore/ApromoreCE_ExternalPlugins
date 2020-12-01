/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.bpmnminer;

import com.raffaeleconforti.wrappers.settings.MiningSettings;
import org.deckfour.xes.model.XLog;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by corno on 2/07/2014.
 */
public interface BPMNMinerService {

    String discoverBPMNModel(XLog log, boolean sortLog, boolean structProcess, int miningAlgorithm, MiningSettings params, int dependencyAlgorithm, double interruptingEventTolerance, double timerEventPercentage,
                             double timerEventTolerance, double multiInstancePercentage, double multiInstanceTolerance,
                             double noiseThreshold, List<String> listCandidates, Map<Set<String>, Set<String>> primaryKeySelections) throws Exception;
}
