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

  protected String sourceNode;
  protected String targetNode;

  protected String sourceContract;
  protected String targetContract;

  protected String encryptedPrivateKey;
  protected String password;

  public AnchoringRunner(final Properties props) {
    this.sourceNode = getProperty(props, "sourceNode");
    this.targetNode = getProperty(props, "targetNode");

    this.sourceContract = getProperty(props, "sourceContract");
    this.targetContract = getProperty(props, "targetContract");

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
      System.out.println("Listening event of " + sourceContract);
      stream();
    } else {
      System.err.println("Neither --kengen nor --listen don't set. For more information, --help");
    }
  }

  protected void stream() {
    try (Wallet sourceChainWallet = supplySourceChainWallet();
        Wallet targetChainWallet = supplyTargetChainWallet()) {

      final EventFilter eventFilter = EventFilter.newBuilder(ContractAddress.of(sourceContract))
          .build();

      final ContractAddress contractAddress = ContractAddress.of(targetContract);
      final TestnetContract contract =
          new SmartContractFactory().create(TestnetContract.class, contractAddress);
      contract.bind(targetChainWallet);
      final Subscription<Event> subscription =
          sourceChainWallet.subscribeEvent(eventFilter, new StreamObserver<Event>() {

            @Override
            public void onNext(Event value) {
              System.err.println("Event received: " + value);
              callBack(contract, value);
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

  @Customizable
  protected void callBack(final TestnetContract contract, final Event eventValue) {
    final List<Object> args = eventValue.getArgs();
    final String owner = (String) args.get(0);
    final int height = (int) args.get(1);
    final int count = (int) args.get(2);
    contract.syncdb(height, owner, count);
  }


  protected Wallet supplySourceChainWallet() {
    return new WalletBuilder()
        .withEndpoint(sourceNode)
        .build(WalletType.Naive);
  }

  protected Wallet supplyTargetChainWallet() {
    final Wallet wallet = new WalletBuilder()
        .withEndpoint(targetNode)
        .build(WalletType.Naive);
    final AergoKey key = AergoKey.of(encryptedPrivateKey, password);
    System.out.println("Currently binded account: " + key.getAddress());
    wallet.saveKey(key, password);
    wallet.unlock(Authentication.of(key.getAddress(), password));
    return wallet;
  }

}
