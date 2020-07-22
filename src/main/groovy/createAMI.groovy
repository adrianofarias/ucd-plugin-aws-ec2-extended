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
final def awsImageName = props['aws_ami_name']
		
def helper = new AWSHelper(airTool)

//Set credentials for login
helper.setCredentials();

//Criar imagem AMI baseada em uma instância
//Comando: aws ec2 create-image

def cliCommand = [awscli,'ec2','create-image']
cliCommand << '--instance-id' << awsInstanceID
cliCommand << '--name' << awsImageName
cliCommand << '--output' << 'text'

def awsImageId = cliCommand.execute().text

if (awsImageId)   {
    awsImageId = awsImageId.substring(0,awsImageId.length()-1)
    println "\nID da imagem AMI criada: "+awsImageId 
    airTool.setOutputProperty("aws-ami-id", awsImageId)
} else  {
    println "\nERRO: Não foi possível criar a imagem AMI"
    System.exit(1)
}

//Aguardar imagem ficar disponível
//Comando: aws ec2 wait image-available
def cliWait = [awscli,'ec2','wait','image-available','--image-id',awsImageId]
cliWait.execute().waitFor()

//Set output properties
airTool.storeOutputProperties();//write the output properties to the file