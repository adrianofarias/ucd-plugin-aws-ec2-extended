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
final def awsAutoScalingGroupName = props['aws_auto_scaling_group_name']
final def awsLaunchConfigurationName = props['aws_launch_configuration_name']

def helper = new AWSHelper(airTool)

//Set credentials for login
helper.setCredentials();

//Criar launch configuration no Autoscaling do EC2.
//Comando: aws autoscaling update-auto-scaling-group

def cliCommand = [awscli,'autoscaling','update-auto-scaling-group']
cliCommand << '--auto-scaling-group-name' << awsAutoScalingGroupName
cliCommand << '--launch-configuration-name' << awsLaunchConfigurationName

def awsAutoScalingGroup = cliCommand.execute()
awsAutoScalingGroup.waitFor()

if (awsAutoScalingGroup.exitValue())   {
    println "\nERRO: Falha ao atualizar o auto-scaling group."
    System.exit(1)
} else  {
    println "\nAuto-scaling Group ${awsAutoScalingGroupName} atualizado com sucesso."
}