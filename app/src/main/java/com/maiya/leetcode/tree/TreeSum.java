package com.maiya.leetcode.tree;

/**
 * Author : ymc
 * Date   : 2020/4/20  18:32
 * Class  : TreeSum
 */
public class TreeSum {
    int sum = Integer.MIN_VALUE;

    public class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode(int x) { val = x; }
    }

    public static void main(String[] args) {



    }

    public int getMax(TreeNode root) {
        if(root == null){
            return 0;
        }
        int leftMax = Math.max(getMax(root.left),0);
        int reghtMax = Math.max(getMax(root.right),0);
        sum = leftMax + reghtMax + root.val;
        return Math.max(leftMax,reghtMax) +root.val;
    }

}
