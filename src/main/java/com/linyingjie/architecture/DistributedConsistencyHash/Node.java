package com.linyingjie.architecture.DistributedConsistencyHash;

import java.util.Objects;

public class Node {
    private String name;
    private long   hash;

    public Node() {
    }

    public Node(String name, long hash) {
        this.name = name;
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getHash() {
        return hash;
    }

    public void setHash(long hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", hash=" + hash +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return hash == node.hash &&
                Objects.equals(name, node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, hash);
    }
}
