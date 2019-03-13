package io.aergo.sample;

import hera.api.model.Authentication;
import hera.api.model.ContractAddress;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.contract.SmartContractFactory;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.util.List;
import java.util.Properties;

public class AnchoringRunner {

  protected String sqlTestNet;
  protected String testNet;

  protected String sqlContract;
  protected String testnetContract;

  protected String encryptedPrivateKey;
  protected String password;

  public AnchoringRunner(final Properties props) {
    this.sqlTestNet = getProperty(props, "sqltestNet");
    this.testNet = getProperty(props, "testNet");

    this.sqlContract = getProperty(props, "sqlContract");
    this.testnetContract = getProperty(props, "testnetContract");

    this.encryptedPrivateKey = props.getProperty("encrypedPrivateKey");
    this.password = props.getProperty("password");
  }

  protected String getProperty(final Properties props, final String key) {
    final String value = props.getProperty(key);
    if (null == value) {
      throw new IllegalArgumentException("Property " + key + " is not set");
    }
    return value;
  }

  public void run(final AnchoringArguments arguments) {
    if (arguments.isKeyGen()) {
      if (null == arguments.getPassword()) {
        System.err.println("You must enter password to encrypt key");
        return;
      }
      final AergoKey key = new AergoKeyGenerator().create();
      System.out.println("Encrypted key: " + key.export(arguments.getPassword()));
      System.out.println("Address: " + key.getAddress());
    } else if (arguments.isListening()) {
      System.out.println("Listening event of " + sqlContract);
      stream();
    } else {
      System.err.println("Neither --kengen nor --listen don't set. For more information, --help");
    }
  }

  public void stream() {
    try (Wallet sqlTestNetWallet = supplySqlTestNetWallet();
        Wallet testNetWallet = supplyTestNetWallet()) {

      final EventFilter eventFilter = EventFilter.newBuilder(ContractAddress.of(sqlContract))
          .build();

      final ContractAddress contractAddress = ContractAddress.of(testnetContract);
      final TestnetContract contract =
          new SmartContractFactory().create(TestnetContract.class, contractAddress);
      contract.bind(testNetWallet);
      final Subscription<Event> subscription =
          sqlTestNetWallet.subscribeEvent(eventFilter, new StreamObserver<Event>() {

            @Override
            public void onNext(Event value) {
              System.err.println("Event received: " + value);
              final List<Object> args = value.getArgs();
              final String owner = (String) args.get(0);
              final int height = (int) args.get(1);
              final int count = (int) args.get(2);
              contract.syncdb(height, owner, count);
            }

            @Override
            public void onError(Throwable t) {
              System.err.println(t);
            }

            @Override
            public void onCompleted() {
              System.err.println("Event finished");
            }
          });

      while (!subscription.isUnsubscribed());
    }
  }

  public Wallet supplySqlTestNetWallet() {
    return new WalletBuilder()
        .withEndpoint(sqlTestNet)
        .build(WalletType.Naive);
  }

  public Wallet supplyTestNetWallet() {
    final Wallet wallet = new WalletBuilder()
        .withEndpoint(testNet)
        .build(WalletType.Naive);
    final AergoKey key = AergoKey.of(encryptedPrivateKey, password);
    System.out.println("Currently binded account: " + key.getAddress());
    wallet.saveKey(key, password);
    wallet.unlock(Authentication.of(key.getAddress(), password));
    return wallet;
  }

}
