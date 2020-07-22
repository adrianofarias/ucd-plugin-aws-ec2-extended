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

//Aguardar por instância em execução
//Comando: aws ec2 wait running-instances

def cliWait = [awscli,'ec2','wait','instance-status-ok']
cliWait << '--instance-ids' << awsInstanceID
cliWait.execute().waitFor()