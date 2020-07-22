import com.urbancode.air.plugin.aws.AWSHelper;

import com.urbancode.air.AirPluginTool;
import com.urbancode.air.CommandHelper;
import groovy.json.JsonSlurper;

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
final def awsLaunchConfigurationName = props['aws_launch_configuration_name']
final def awsAMIID = props['aws_ami_id']
final def awsInstanceType = props['aws_instance_type']
final def awsKeyPair = props['aws_keypair_name']
final def awsSecurityGroups = props['aws_security_groups']

def helper = new AWSHelper(airTool)

//Set credentials for login
helper.setCredentials();

//Criar launch configuration no Autoscaling do EC2.
//Comando: aws autoscaling create-launch-configuration

def cliCommand = [awscli,'autoscaling','create-launch-configuration']
cliCommand << '--launch-configuration-name' << awsLaunchConfigurationName
cliCommand << '--image-id' << awsAMIID
cliCommand << '--instance-type' << awsInstanceType
if (awsKeyPair) {cliCommand << '--key-name' << awsKeyPair}
if (awsSecurityGroups) {cliCommand << '--security-groups' << awsSecurityGroups}

def launchConfiguration = cliCommand.execute()
launchConfiguration.waitFor()

if (launchConfiguration.exitValue())   {
    println "\nERRO: Falha ao criar o launch configuration."
    System.exit(1)
} else  {
    println "\nLaunch Configuration ${awsLaunchConfigurationName} criado com sucesso."
}