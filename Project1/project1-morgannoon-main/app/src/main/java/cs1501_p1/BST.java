/**
 * BST
 * 
 * @author Morgan Noonan
 */
package cs1501_p1;

import java.util.NoSuchElementException;

public class BST<T extends Comparable<T>> implements BST_Inter<T> {
    private BTNode<T> root;

    public BST() {
        root = null;
    }

    /**
     * Method getRoot
     * returns the root node
     * 
     * @return {BTNode<T>} root
     */
    public BTNode<T> getRoot() {
        return root;
    }

    /**
     * Method setRoot
     * sets the root node key
     * 
     * @param {BTNode<T>} root
     */
    public void setRoot(BTNode<T> root) {
        this.root = root;
    }

    /**
     * Method put
     * inserts key into BST
     * 
     * @param {T} key
     */
    public void put(T key) {
        if (key == null)
            throw new IllegalArgumentException("calls put(key) with a null key");
        if (root == null) {
            root = new BTNode<T>(key);
        } else if (!contains(key)) {
            BTNode<T> currNode = root;
            int comp;
            while (true) {
                comp = key.compareTo(currNode.getKey());
                if (comp < 0) {
                    if (currNode.getLeft() != null) {
                        currNode = currNode.getLeft();
                    } else {
                        currNode.setLeft(new BTNode<T>(key));
                        return;
                    }
                } else if (comp > 0) {
                    if (currNode.getRight() != null) {
                        currNode = currNode.getRight();
                    } else {
                        currNode.setRight(new BTNode<T>(key));
                        return;
                    }
                }
            }
        }
    }

    /**
     * Method contains
     * checks true if given key is in the tree, otherwise false
     * 
     * @param {T} key
     * @return {boolean} True or False
     */
    public boolean contains(T key) {
        if (root != null) {
            BTNode<T> currNode = root;
            int comp;
            while (currNode != null) {
                comp = key.compareTo(currNode.getKey());
                if (comp < 0) {
                    currNode = currNode.getLeft();
                } else if (comp > 0) {
                    currNode = currNode.getRight();
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method delete
     * deletes given key from BST
     * 
     * @param {T} key
     */
    public void delete(T key) {
        if (key == null)
            throw new IllegalArgumentException("calls delete(key) with a null key");
        if (!contains(key)) {
            throw new NoSuchElementException("key does not exist");
        } else {
            BTNode<T> deleting = root;
            BTNode<T> deletPrev = root;
            boolean leftChild = false;
            while (deleting.getKey().compareTo(key) != 0) {
                if (key.compareTo(deleting.getKey()) < 0) {
                    deletPrev = deleting;
                    deleting = deleting.getLeft();
                    leftChild = true;
                } else {
                    deletPrev = deleting;
                    deleting = deleting.getRight();
                    leftChild = false;
                }
            }
            // Case 1: Deleting node with no children or one child
            if (deleting.getLeft() == null || deleting.getRight() == null) {
                if (deleting.getLeft() != null) {
                    if (leftChild)
                        deletPrev.setLeft(deleting.getLeft());
                    else
                        deletPrev.setRight(deleting.getLeft());
                } else if (deleting.getRight() != null) {
                    if (leftChild)
                        deletPrev.setLeft(deleting.getRight());
                    else
                        deletPrev.setRight(deleting.getRight());
                } else {
                    if (leftChild)
                        deletPrev.setLeft(null);
                    else
                        deletPrev.setRight(null);
                }
                return;
            }

            // Case 2: Node with two children
            // Find the inorder successor (lergest node in the left subtree)
            // Delete the inorder successor from the left subtree
            // Update the key of the current node with the key of the inorder successor
            BTNode<T> nearest = deleting.getLeft();
            BTNode<T> nearPrev = deleting;
            if (nearest.getRight() == null) {
                nearest.setRight(deleting.getRight());
                if (leftChild)
                    deletPrev.setLeft(nearest);
                else
                    deletPrev.setRight(nearest);
                return;
            }
            while (nearest.getRight() != null) {
                nearPrev = nearest;
                nearest = nearest.getRight();
            }

            if (leftChild)
                deletPrev.setLeft(nearest);
            else
                deletPrev.setRight(nearest);
            nearPrev.setRight(nearest.getLeft());
            nearest.setLeft(deleting.getLeft());
            nearest.setRight(deleting.getRight());
        }

    }

    /**
     * Method height
     * returns the height of the BST
     * no root has a height of 0, only the root is a height of 1
     * 
     * @return {int} height
     */
    public int height() {
        return heightMax(root);
    }

    private int heightMax(BTNode<T> root) {
        if (root == null) {
            return 0;
        }
        int l = heightMax(root.getLeft());
        int r = heightMax(root.getRight());
        if (l > r) {
            return l + 1;
        } else {
            return r + 1;
        }
    }

    /**
     * Method isBalanced
     * returns true is BST is balanced, otherwise false
     * 
     * @returns
     */
    public boolean isBalanced() {
        return balanceRec(root);
    }

    private boolean balanceRec(BTNode<T> root) {
        int l;
        int r;
        if (root == null) {
            return true;
        }
        l = h(root.getLeft());
        r = h(root.getRight());

        if (Math.abs(l - r) <= 1 && balanceRec(root.getLeft()) && balanceRec(root.getRight())) {
            return true;
        }
        return false;
    }

    private int h(BTNode<T> root) {
        if (root == null) {
            return 0;
        }
        return 1 + Math.max(h(root.getLeft()), h(root.getRight()));
    }

    /**
     * Method inOrderTraversal
     * prints the in-order traversal of the BST
     * 
     * @return {String} orderString
     */
    public String inOrderTraversal() {
        String orderString = "";
        return inOrder(root, orderString);
    }

    private String inOrder(BTNode<T> root, String orderString) {
        if (root.getLeft() != null) {
            orderString += inOrder(root.getLeft(), orderString);
        }
        if (orderString.equals("")) {
            orderString += root.getKey();
        } else
            orderString += ":" + root.getKey();

        if (root.getRight() != null) {
            orderString = inOrder(root.getRight(), orderString);
        }
        return orderString;
    }

    /**
     * Method serialize
     * prints the pre-order traversal of the BST
     * R(key) for root, I(key) for interior node, L(key) for leaf, X(NULL) for null
     * node
     * 
     * @return {String} str
     */
    public String serialize() {
        String str = "";
        if (root != null) {
            str = "R(" + root.getKey() + ")";
        } else if (root == null) {
            return "R(NULL)";
        }
        str = interior(root.getLeft(), str);
        str = interior(root.getRight(), str);
        return str;
    }

    private String interior(BTNode<T> root, String str) {
        if (root == null) {
            return str += ",X(NULL)";
        }

        // Construct the serialization format
        if (root.getLeft() == null && root.getRight() == null) {
            return str += ",L(" + root.getKey() + ")";
        } else {
            str += ",I(" + root.getKey() + ")";
        }

        str = interior(root.getLeft(), str);
        str = interior(root.getRight(), str);

        return str;
    }

    /**
     * Method reverse
     * returns the reversed BST
     * 
     * @return {BST_Inter<T>} reversedTree
     */
    public BST_Inter<T> reverse() {
        BST<T> reversedTree = new BST<T>();
        BTNode<T> rRoot = copy(root, reversedTree);
        revRec(rRoot);
        reversedTree.root = rRoot;
        return reversedTree;
    }

    private BTNode<T> copy(BTNode<T> root, BST_Inter<T> newTree) {
        if (root == null || root.getKey() == null) {
            return null;
        }
        // copy tree into newTree
        BTNode<T> newRoot = new BTNode<T>(root.getKey());
        newTree.put(newRoot.getKey());
        newRoot.setLeft(copy(root.getLeft(), newTree));
        newRoot.setRight(copy(root.getRight(), newTree));
        return newRoot;
    }

    private void revRec(BTNode<T> root) {
        if (root == null) {
            return;
        }
        BTNode<T> temp = root.getLeft();
        root.setLeft(root.getRight());
        root.setRight(temp);
        revRec(root.getLeft());
        revRec(root.getRight());
    }
}