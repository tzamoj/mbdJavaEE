package editorUser;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*By Thomas Zamojski, Nov 6, 2015
 * 
 * The interface for the client side observer pattern
 */
public interface Observer extends Remote{
  void textUpdate(String text) throws RemoteException;
  void selectionUpdate(int a, int b) throws RemoteException;
}