package com.maiya.leetcode.tree;

import java.util.HashSet;
import java.util.List;

/**
 * Author : ymc
 * Date   : 2020/4/26  19:26
 * Class  : SameList
 */
public class OfferEasy {

    /**
     * 判断相同值
     * @param nums
     * @return
     */
    public static int findRepeatNumber(int[] nums) {
        HashSet<Integer> set = new HashSet<>();
        int r = -1;
        for(int nb:nums){
            if(!set.add(nb)){
                r = nb;
                break;
            }
        }
        return r;
    }

    /**
     * 发现number 在二维数组中
     * @param matrix
     * @param target
     * @return
     */
    public boolean findNumberIn2DArray(int[][] matrix, int target) {
        if(matrix.length==0){
            return false;
        }
        int rows = matrix.length;
        int cl= matrix[0].length;
        for(int r =0;r<rows ;r++){
            for(int c = 0;c<cl;c++){
                if(matrix[r][c]==target){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 替换空格
     * @param s
     * @return
     */
    public String replaceSpace(String s) {
        int sLength = s.length();
        char [] chars = new char[sLength*3];
        int index = 0;
        for(int x=0;x<sLength;x++){
            char c = s.charAt(x);
            if(c==' '){
                chars[index++] = '%';
                chars[index++] = '2';
                chars[index++] = '0';
            }else{
                chars[index++]=c;
            }
        }
        String result = new String(chars,0,index);
        return result;
    }

    public class ListNode {
      int val;
      ListNode next;
      ListNode(int x) { val = x; }
    }

    /**
     * 反转输出
     * @param head
     * @return
     */
    public int[] reversePrint(ListNode head) {

        return null;
    }

}
