package io.aergo.sample;

import hera.contract.SmartContract;

public interface TestnetContract extends SmartContract {

  void syncdb(long height, String owner, int count);

}
