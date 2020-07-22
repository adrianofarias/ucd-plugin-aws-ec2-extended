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
final def awsInstanceID = props['aws_instance_id']
		
def helper = new AWSHelper(airTool)

//Set credentials for login
helper.setCredentials();

//Solicitar exclusão (terminate) de uma instância EC2.
//Comando: aws ec2 terminante-instances

def cliCommand = [awscli,'ec2','terminate-instances']
cliCommand << '--instance-ids' << awsInstanceID
cliCommand << '--query' << 'TerminatingInstances[0].{InstanceId:InstanceId}'
cliCommand << '--output' << 'text'

def terminatingInstance = cliCommand.execute().text

if (terminatingInstance)   {
    println "\nInstancia sendo excluída: "+terminatingInstance 
} else  {
    println "\nERRO: Não foi possível solicitar exclusão a instancia."
    System.exit(1)
}