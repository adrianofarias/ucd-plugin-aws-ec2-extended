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
final def awsBaseAMIID = props['aws_base_ami_id']
final def awsInstanceName = props['aws_instance_name']
final def awsInstanceType = props['aws_instance_type']
final def awsKeyPairName = props['aws_keypair_name']
final def awsSecurityGroups = props['aws_security_groups']
		
def helper = new AWSHelper(airTool)

//Set credentials for login
helper.setCredentials();

//Criar instância no AWS EC2
//Comando: aws ec2 run-instances

def cliCreate = [awscli,'ec2','run-instances']
cliCreate << '--image-id' << awsBaseAMIID
cliCreate << '--instance-type' << awsInstanceType
cliCreate << '--key-name' << awsKeyPairName
cliCreate << '--security-groups' << awsSecurityGroups
cliCreate << '--tag-specifications' << "ResourceType=instance,Tags=[{Key=Name,Value=${awsInstanceName}}]"
cliCreate << '--query' << 'Instances[0].{InstanceId:InstanceId}'
cliCreate << '--output' << 'text'

def instanciaEC2ID = cliCreate.execute().text
instanciaEC2ID = instanciaEC2ID.substring(0,instanciaEC2ID.length()-1)

if (instanciaEC2ID)   {
    println "\nID da instancia criada: "+instanciaEC2ID 
    airTool.setOutputProperty("aws-instance-id", instanciaEC2ID)
} else  {
    println "\nERRO: Não foi possível criar instância EC2"
    System.exit(1)
}

//Obter IP Público da intância
//Comando: aws ec2 describe-instances

def cliDescribe = ['aws','ec2','describe-instances']
cliDescribe << '--instance-ids' << instanciaEC2ID
cliDescribe << '--query' << 'Reservations[0].Instances[0].{PublicIpAddress:PublicIpAddress}'
cliDescribe << '--output' << 'text'

def instanciaEC2PublicIP = cliDescribe.execute().text

if  (instanciaEC2PublicIP)  {
    instanciaEC2PublicIP = instanciaEC2PublicIP.substring(0,instanciaEC2PublicIP.length()-1)
    println "\nIP Público: "+instanciaEC2PublicIP
    airTool.setOutputProperty("aws-instance-public-ip", instanciaEC2PublicIP)
} else  {
    println "\nERRO: Não foi possível obter IP público da instância."
    System.exit(1)
}

//Set output properties
airTool.storeOutputProperties();//write the output properties to the file