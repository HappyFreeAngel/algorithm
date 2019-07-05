package com.linyingjie.architecture.DistributedConsistencyHash;

public class VirtualNode extends Node {
//    private String nodeName;//这个名字指的是其对应的真实的物理服务器节点的名字
//    private long nodeNameHash;

    public VirtualNode(String nodeName, long nodeNameHash) {
        super(nodeName,nodeNameHash);
    }
}