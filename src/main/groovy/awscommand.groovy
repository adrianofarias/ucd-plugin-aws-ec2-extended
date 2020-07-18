import com.urbancode.air.plugin.aws.AWSHelper;

import com.urbancode.air.AirPluginTool;
import com.urbancode.air.CommandHelper;

final airTool = new AirPluginTool(args[0], args[1])

/* Here we call getStepProperties() to get a Properties object that contains the step properties
 * provided by the user. 
 */
final def props = airTool.getStepProperties()

/* This is how you retrieve properties from the object. You provide the "awscli" attribute of the 
 * <property> element.
 * 
 */

final def awscli = props['awscli']
final def command = props['command']
final def subcommand = props['subcommand']
final def arguments = props['arguments']

def helper = new AWSHelper(airTool)

//Set credentials for login
helper.setCredentials();

def cli = [awscli]

cli << command
cli << subcommand
cli << arguments

cmdHelper.runCommand("Running aws cli", cli)

//Set an output property
airTool.setOutputProperty("aws_command", "${cli}");
airTool.storeOutputProperties();//write the output properties to the file