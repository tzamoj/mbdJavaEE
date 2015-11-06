/**
 * The interface for the remote methods of the Engine. Pertains to the observer pattern. 
 */

package editorEngine;

import editorUser.Observer;
import java.rmi.Remote;
import java.rmi.RemoteException;

//import javax.ws.rs.core.Response;


public interface EditorEngine extends Remote {
//  void cut() throws RemoteException;
//  void copy() throws RemoteException;
//  void paste() throws RemoteException;
//  void setSelection(Integer start, Integer length) throws RemoteException;
//  void insert(String s) throws RemoteException;
//  Response contents();
//  void evaluate() throws RemoteException;
  void addObserver(Observer o) throws RemoteException;
  void removeObserver(Observer o) throws RemoteException;
}
