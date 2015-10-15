/**
 * The interface for the editorEngine. Its functionalities include
 *  - Editing: usual text editing and selection handling.
 *  - Rcode: execute R code via evaluate
 *  - Subject: subject in an Observer pattern.
 * 
 */

package editorEngine;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.ws.rs.core.Response;

import editorUser.Observer;

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
