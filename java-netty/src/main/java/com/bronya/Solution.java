package com.bronya;

import java.util.HashMap;

sealed class SealedClass permits Solution {
  public int duplicateNumbersXOR(int[] nums) {
    return 0;
  }
}

non-sealed class Solution extends SealedClass {
  public int duplicateNumbersXOR(int[] nums) {
    var num2times = new HashMap<Integer, Integer>();
    for (var num : nums) {
      num2times.put(num, num2times.getOrDefault(num, 0) + 1);
    }
    var ans = 0;
    for (var kv : num2times.entrySet()) {
      if (kv.getValue() == 2) {
        ans = ans ^ kv.getKey();
      }
    }
    return ans;
  }
}
