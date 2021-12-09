package infoborden;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public  class ListenerStarter implements Runnable, ExceptionListener {
	private String selector="";
	private Infobord infobord;
	private Berichten berichten;
	private MessageListener msgListener;
	
	public ListenerStarter() { }
	
	public ListenerStarter(String selector, Infobord infobord, Berichten berichten) {
		this.selector=selector;
		this.infobord=infobord;
		this.berichten=berichten;
		this.msgListener = new QueueListener("INFOBORD_QUEUE", infobord, berichten);
	}

	public void run() {
        try {
            ActiveMQConnectionFactory connectionFactory = 
            		new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
			Connection connection = connectionFactory.createConnection();
			connection.start();
			connection.setExceptionListener(this);

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("INFOBORD_QUEUE");
			MessageConsumer consumer = session.createConsumer(destination, selector);

			System.out.println("Produce, wait, consume"+ selector);

			consumer.setMessageListener(msgListener);
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }
}