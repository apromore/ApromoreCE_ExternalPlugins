# ApromoreCE_ExternalPlugins
This repository contains external plugins for Apromore CE contributed by the open-source community. These plugins are provided under the LGPL 3.0 license or other open-source license.

Apromore Pty Ltd makes this code available ‘as is’ and does not check the currency, accuracy, reliability, performance, completeness, suitability or workability of this code or whether any code or other material uploaded by the contributors infringe anyone’s intellectual property rights. You use this code at your own risk.

To the fullest extent permitted by law, Apromore Pty Ltd excludes all liability whether in contract, tort (including negligence), statute or otherwise for any direct or indirect losses, damages, expenses, claims and liability that you may incur as a result or arising from any use of this code, even if Apromore Pty Ltd has been advised of the possibility of such damages. To the extent legislation does not allow particular liability to be excluded, it is limited to the fullest extent permitted by such legislation.

## Installation instructions
This is not a standalone repository but rather a collection of independent plugins. All the plugins are meant to be run as a part of [Apromore Community Edition](https://github.com/apromore/ApromoreCE), which is open source. As such, you should first check out the repository above and follow these [installation instructions](https://github.com/apromore/ApromoreCE/blob/v7.15/README.md#installation-instructions) to get Apromore Community Edition up and running. Keep the prompt/terminal window open, once installed.

Next, build the required plugin(s). For example, to build the Long Distance Portal Plugin, you can do the following:

```bash
cd external-plugins/Long-Distance-Portal-Plugin
mvn clean install
```

Finally, once the plugin is built, copy it into the directory where you installed ApromoreCE

```bash
cp target/long-distance-portal-plugin-1.1.jar  /wherever/you/installed/ApromoreCE/ApromoreCore/Apromore-Assembly/virgo-tomcat-server-3.6.4.RELEASE/pickup
```

If you are adding multiple plugins, make sure to add them in the correct order (dependencies first).

If the plugin provides user interface, it will appear under the portal menu.

To stop the plugin, simply delete it from the `pickup` folder above.

For the list of commonly encountered issues, please consult [this page](https://github.com/apromore/ApromoreCore#common-problems).
