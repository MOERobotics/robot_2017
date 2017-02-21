package org.usfirst.frc.team365.robot;

import java.util.HashMap;
import java.util.NoSuchElementException;
import javax.management.openmbean.KeyAlreadyExistsException;

public class SharedVariables {
	private static boolean isInit = false;
	private static HashMap<String, HashMap<String,Object>> sharedVariables;
	
	public static void init() {
		if (isInit) return;
		sharedVariables = new HashMap<String, HashMap<String,Object>>();
		isInit = true;
	}
	
	public static Object get(String keyspace_name, String key) throws NoSuchElementException {
		HashMap<String,Object> keyspace = getKeyspace(keyspace_name);
		
		if (!sharedVariables.containsKey(keyspace_name))
			throw new NoSuchElementException(String.format(
				"Keyspace %s has not registered the key %s as a valid key!", 
				keyspace_name,
				key
			));
		
		return keyspace.get(key);		
	}
	
	public static void set(String keyspace_name, String key, Object value) throws NoSuchElementException {
		HashMap<String,Object> keyspace = getKeyspace(keyspace_name);
		
		if (!sharedVariables.containsKey(keyspace_name))
			throw new NoSuchElementException(String.format(
				"Keyspace %s has not registered the key %s as a valid key!", 
				keyspace_name,
				key
			));
		
		keyspace.put(key, value);
	}
	
	public static void registerKey (String keyspace_name, String key) throws NoSuchElementException,KeyAlreadyExistsException {
		HashMap<String,Object> keyspace = getKeyspace(keyspace_name);
		
		if (sharedVariables.containsKey(keyspace_name))
			throw new KeyAlreadyExistsException(String.format(
				"Keyspace %s is attempting to register the key %s twice!", 
				keyspace_name,
				key
			));
		
		keyspace.put(key, null);
	}
	public static void registerKey (String keyspace_name, String key, Object object) throws NoSuchElementException,KeyAlreadyExistsException {
		registerKey(keyspace_name,key);
		set(keyspace_name,key,object);
	}

	public static void registerKeyspace (String keyspace_name) throws KeyAlreadyExistsException {
		if (sharedVariables.containsKey(keyspace_name))
			throw new KeyAlreadyExistsException(String.format(
				"Attempting to register keyspace %s twice!", 
				keyspace_name
			));
		
		sharedVariables.put(keyspace_name,new HashMap<String,Object>());
	}
	
	private static HashMap<String,Object> getKeyspace (String keyspace_name) throws NoSuchElementException {
		if (!sharedVariables.containsKey(keyspace_name))
			throw new NoSuchElementException(String.format(
				"No class registered that will accept messages stored in %s!", 
				keyspace_name
			));
		
		return sharedVariables.get(keyspace_name);
		
	}
	
}