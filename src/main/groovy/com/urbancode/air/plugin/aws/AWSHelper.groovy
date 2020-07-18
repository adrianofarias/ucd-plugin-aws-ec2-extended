/*
* Licensed Materials - Property of IBM Corp.
* IBM UrbanCode Build
* IBM UrbanCode Deploy
* IBM UrbanCode Release
* IBM AnthillPro
* (c) Copyright IBM Corporation 2002, 2013. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/
package com.urbancode.air.plugin.aws;

import com.urbancode.air.CommandHelper;

public class AWSHelper {
    def apTool;
    def props;
    def awsCli
    CommandHelper cmdHelper;

    public AWSHelper(def apTool) {
        this.apTool = apTool;
        this.props = apTool.getStepProperties();
        this.awsCli = props['awscli']
        this.cmdHelper = new CommandHelper(new File("."))
    }

    public def setCredentials() {


        //Credentials - to create .credentials file
        final def awsAccessKeyId = props['aws_access_key_id']
        final def awsSecretAccessKey = props['aws_secret_access_key']

        cmdHelper.runCommand("Set access key", "${awsCli} configure set aws_access_key_id ${awsAccessKeyId}")
        cmdHelper.runCommand("Set secret access key", "${awsCli} configure set aws_secret_access_key ${awsSecretAccessKey}")

        //Configuration - to create .config file
        final def awsRegion = props['aws_region']

        cmdHelper.runCommand("Set aws region", "${awsCli} configure set region ${awsRegion}")

    }
/*
    public def startInstances(def ami, def number, def instanceType, def keyPair, def zone,
            def group, def options) {
        RunInstancesRequest req = new RunInstancesRequest()
                                  .withImageId(ami)
                                  .withMinCount(Integer.valueOf(number))
                                  .withMaxCount(Integer.valueOf(number))
                                  .withInstanceType(instanceType);
        
        if (keyPair) {
            req = req.withKeyName(keyPair);
        }
        
        if (zone) {
            Placement placement = new Placement(zone);
            req = req.withPlacement(placement);
        }
        
        if (group) {
            req = req.withSecurityGroups(group.split(','));
        }
        
        if (options) {
            req = req.withUserData(options);
        }
        
        println "Creating $number instances of $ami";
        RunInstancesResult result = ec2.runInstances(req);
        List<Instance> instances = result.getReservation().getInstances();
        
        def insts = instances.collect { return it.getInstanceId() };
                                  
        apTool.setOutputProperty("instances", insts.join(','));
        
        return insts;
    }

    public def gatherInstanceDetails(def instanceIds) {
        def dnses = [];
        def privateIPs = [];
        
        DescribeInstancesRequest describeRequest = new DescribeInstancesRequest().withInstanceIds(instanceIds);
        DescribeInstancesResult describeResult = ec2.describeInstances(describeRequest);
        
        List<Instance> describeInstances = new ArrayList<Instance>();
        List<Reservation> reservations = describeResult.getReservations();
        reservations.each { reservation ->
            List<Instance> currInstances = reservation.getInstances();
            describeInstances.addAll(currInstances);
        }
        
        instanceIds.each { instanceId ->
            Instance instance = describeInstances.find { it.getInstanceId() == instanceId };
            dnses << instance.getPublicDnsName();
            privateIPs << instance.getPrivateIpAddress();
        }
            
        apTool.setOutputProperty("dns", dnses.join(','));
        apTool.setOutputProperty("privateIPs", privateIPs.join(','));
    }
    
    public def waitForInstances(def instanceIds, def timeout, def status) {
        def badStates = [:];
        for (def inst : instanceIds) {
            badStates.put(inst, "bad");
        }
        
        Long startTime = System.currentTimeMillis();
        while (!badStates.isEmpty() && System.currentTimeMillis() - startTime < timeout) {
            println "Waiting for instances to achieve "+status+" state: "+badStates
            
            def newBadStates = [:];
            def badInstanceIds = badStates.collect { it.getKey() };
        
            DescribeInstancesRequest req = new DescribeInstancesRequest().withInstanceIds(instanceIds);
            DescribeInstancesResult result = ec2.describeInstances(req);
        
            List<Instance> instances = new ArrayList<Instance>();
            List<Reservation> reservations = result.getReservations();
            reservations.each { reservation ->
                List<Instance> currInstances = reservation.getInstances();
                instances.addAll(currInstances);
            }
        
            badInstanceIds.each { instanceId ->
                Instance instance = instances.find { it.getInstanceId() == instanceId };
        
                if (!instance.getState().getName().equals(status)) {
                    newBadStates.put(instanceId, status);
                }
            }
            badStates = newBadStates;
            Thread.sleep(5000);
        }

        if (badStates.isEmpty()) {
            println "All instances are in state: "+status;
        }
        else {
            println "The following instances were not in the expected state after the timeout: "+badStates;
            System.exit(1);
        }
    }*/
}

