package com.linyingjie.architecture.DistributedConsistencyHash;

import java.util.*;
import java.util.zip.CRC32;

//https://blog.csdn.net/u010412719/article/details/53863219
/**
 * 算法的具体原理如下：
 *
 * 先构造一个长度为2^32的整数环（这个环被称为一致性Hash环），根据节点名称的Hash值（其分布为[0, 2^32-1]）将服务器节点放置在这个Hash环上，
 * 然后根据数据的Key值计算得到其Hash值（其分布也为[0, 2^32-1]），接着在Hash环上顺时针查找距离这个Key值的Hash值最近的服务器节点，
 * 完成Key到服务器的映射查找。
 *
 * 一致性Hash的原理图如下：
 * ---------------------
 * https://blog.csdn.net/u010412719/article/details/53863219
 */

public class ConsistentHashImplement implements ConsistentHash {
    //保存虚拟服务器节点节点
    List<VirtualNode> virtualNodes = new ArrayList<VirtualNode>();
    //每个物理节点对应的虚拟节点的个数
    private  int virtualFactor = 5;
    final int DEFAULT_VIRTUAL_FACTOR=5;

    public ConsistentHashImplement(int virtualFactor) {
        this.virtualFactor = virtualFactor;
    }

    public ConsistentHashImplement() {
        this.virtualFactor =5;
    }

    public int getNodeCount(){
        return virtualNodes.size();
    }

    public void addNode(String serverName){
        if(serverName==null){
            return;
        }

        //每个物理节点对应virtualFactor个虚服务器节点
        for(int i = 0; i< virtualFactor; i++){
            //这里假设，虚拟节点的名字为类似这样的形式：serverName+"&&VN"+i，这样方便从虚拟节点得到物理节点
            String virtualServerNodeName = serverName+"&&VN"+i;
            long hash = getStringHashValue(virtualServerNodeName);
            VirtualNode vsNode = new VirtualNode(serverName, hash);
            virtualNodes.add(vsNode);
        }

        //将virtualServerNodes进行排序
        Collections.sort(virtualNodes,
                (VirtualNode node1, VirtualNode node2)-> {
                    if(node1.getHash()<node2.getHash()){
                        return -1;
                    }
                    return 1;
                }
        );

    }

    public long getStringHashValue(String inputString) {
        return getDataHashValue(inputString.getBytes());
    }

    public long getDataHashValue(byte[] inputData) {
        CRC32 crc32 = new CRC32();
        crc32.update(inputData);
        return crc32.getValue();
    }

    //删除服务器节点,即要删除其物理服务器节点对应的所有虚拟节点
    public void delNode(String serverName){

        Set<VirtualNode> tempSet= new HashSet<>();
        for(int i = 0; i< virtualNodes.size(); i++){
            VirtualNode node = virtualNodes.get(i);
            if(node.getName().contains(serverName)){//这里用了contain查找，这里就把该物理服务器节点对应的虚拟节点都删除了
                tempSet.add(node);
            }
        }
        virtualNodes.removeAll(tempSet);
    }

    /**
     * 输入数据，返回虚拟节点
     * @param data
     * @return
     */
    public VirtualNode findNodeByData(byte[] data){
        long hashValue= getDataHashValue(data);
        return findNodeByHashValue(hashValue);
    }

    //得到应当路由到的结点
    public VirtualNode  findNodeByHashValue(long hashValue){
        //在VirtualServerNode中找到大于hash且离其最近的的那个VirtualNode,由于serverNodes是升序排列的，因此，找到的第一个大于hash的就是目标节点
        for(VirtualNode node : virtualNodes){  //这个会有性能瓶颈的， to do ???
            if(node.getHash()>hashValue){ //顺时针方向的那个节点, virtualNodes 是有序的节点.
                return node;
            }
        }
        //如果没有找到，则说明此key的hash值比所有服务器节点的hash值都大，因此返回最小hash值的那个Server节点
        return virtualNodes.get(0);

    }


    public void printAllNodes(){
        System.out.println("所有节点信息如下：");
        for(VirtualNode node: virtualNodes){
            System.out.println(node.getName()+":"+node.getHash());
        }
    }

    public static void main(String[] args){

        ConsistentHashImplement ch = new ConsistentHashImplement();
        //添加一系列的服务器节点
        String[] servers = {"192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111",
                "192.168.0.3:111", "192.168.0.4:111", "192.168.0.5:111", "192.168.0.6:111", "192.168.0.7:111", "192.168.0.8:111", "192.168.0.9:111", "192.168.0.10:111"
                , "192.168.0.11:111", "192.168.0.12:111", "192.168.0.13:111", "192.168.0.14:111", "192.168.0.15:111"};


        for(String server:servers){
            ch.addNode(server);
        }
        //打印输出一下服务器节点
        ch.printAllNodes();
        System.out.println("总共有"+ch.getNodeCount()+"个虚拟服务器.");

        //看看下面的客户端节点会被路由到哪个服务器节点
//        String[] remoteClientArray = {"172.30.1.1:1111", "172.30.2.1:2222", "172.30.3.1:3333", "172.30.4.1:4444", "172.30.5.1:5555", "172.30.6.1:6666", "172.30.7.1:7777"
//        };
        List<String> remoteClientList=new LinkedList<>();

        for(int network=1;network<10;network++) {
            for (int i = 2; i < 254; i++) {
               String remoteClient="172.30."+network+"."+i+":"+network+""+network+""+network+""+network;
               remoteClientList.add(remoteClient);
            }
        }



        System.out.println("此时，各个客户端的路由情况如下：");
        for(String client : remoteClientList){
            VirtualNode virtualNode = ch.findNodeByData(client .getBytes());
            System.out.println(client +","+ ch.getStringHashValue(client )+"------->"+
                    virtualNode.getName()+","+ virtualNode.getHash());
        }

        //如果由一个服务器节点宕机，即需要将这个节点从服务器集群中移除
        String deleteNodeName=servers[(int)(Math.random()*servers.length)];
        ch.delNode(deleteNodeName);

        System.out.println("删除节点"+deleteNodeName+"后，再看看同样的客户端的路由情况，如下：");
        for(String node:remoteClientList){
            VirtualNode virtualNode = ch.findNodeByData(node.getBytes());
            System.out.println(node+","+ ch.getStringHashValue(node)+"------->"+
                    virtualNode.getName()+","+ virtualNode.getHash());
        }
    }
}
