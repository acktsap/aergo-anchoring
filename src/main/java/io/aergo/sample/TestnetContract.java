package io.aergo.sample;

import hera.contract.SmartContract;

/**
 * Customize according to contract callback method to call.
 */
@Customizable
public interface TestnetContract extends SmartContract {

  void syncdb(long height, String owner, int count);

}
