package org.example.domain.supplementary;

/**
 * @author aram.azatyan | 2/22/2024 8:29 PM
 */
public class Node<E> {
    protected E value;
    protected Node<E> parent;
    protected Node<E> left;
    protected Node<E> right;

    public Node(E value, Node<E> parent, Node<E> left, Node<E> right) {
        this.value = value;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }

    public boolean isInternal() {
        return left != null || right != null;
    }

    public boolean isExternal() {
        return left == null && right == null;
    }

    public Node(E value) {
        this.value = value;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public Node<E> getParent() {
        return parent;
    }

    public void setParent(Node<E> parent) {
        this.parent = parent;
    }

    public Node<E> getLeft() {
        return left;
    }

    public void setLeft(Node<E> left) {
        this.left = left;
    }

    public Node<E> getRight() {
        return right;
    }

    public void setRight(Node<E> right) {
        this.right = right;
    }
}