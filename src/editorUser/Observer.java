package editorUser;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Observer extends Remote{
  void textUpdate(String text) throws RemoteException;
  void selectionUpdate(int a, int b) throws RemoteException;
}