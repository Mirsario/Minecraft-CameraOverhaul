package mirsario.cameraoverhaul.fabric.abstractions;

import java.lang.reflect.*;
import net.fabricmc.loader.api.*;

public final class ReflectionUtils
{
	private static final MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();

	// Classes

	public static Class<?> TryGetClass(String fullClassName)
	{
		try {
			return GetClass(fullClassName);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static Class<?> GetClass(String className) throws ClassNotFoundException
	{
		return Class.forName(className);
	}

	public static Class<?> TryGetObfuscatedClass(String fullIntermediaryClassName)
	{
		try {
			return GetObfuscatedClass(fullIntermediaryClassName);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static Class<?> GetObfuscatedClass(String intermediaryClassName) throws ClassNotFoundException
	{
		String mappedTypeName = mappingResolver.mapClassName("intermediary", intermediaryClassName);
		Class<?> type = Class.forName(mappedTypeName);

		return type;
	}

	// Methods

	public static Method TryGetMethod(Class<?> type, String methodName, Class<?>[] parameterTypes)
	{
		try {
			return GetMethod(type, methodName, parameterTypes);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static Method GetMethod(Class<?> type, String methodName, Class<?>[] parameterTypes)
		throws NoSuchMethodException, ClassNotFoundException
	{
		return type.getMethod(methodName, parameterTypes);
	}

	public static Method TryGetObfuscatedMethod(String intermediaryClassName, String intermediaryMethodName, String methodDescriptor, Class<?>[] parameterTypes)
	{
		try {
			return GetObfuscatedMethod(intermediaryClassName, intermediaryMethodName, methodDescriptor, parameterTypes);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static Method GetObfuscatedMethod(String intermediaryClassName, String intermediaryMethodName, String methodDescriptor, Class<?>[] parameterTypes)
		throws NoSuchMethodException, ClassNotFoundException
	{
		Class<?> type = GetObfuscatedClass(intermediaryClassName);
		String mappedMethodName = mappingResolver.mapMethodName("intermediary", intermediaryClassName, intermediaryMethodName, methodDescriptor);
		Method method = type.getMethod(mappedMethodName, parameterTypes);

		return method;
	}

	public static Object TryInvokeMethod(Method method, Object instance, Object... args)
	{
		try {
			return method.invoke(instance, args);
		}
		catch (Exception e) {
			return null;
		}
	}

	// Constructors

	public static Constructor<?> TryGetConstructor(Class<?> type, Class<?>... parameters)
	{
		try {
			return GetConstructor(type, parameters);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static Constructor<?> GetConstructor(Class<?> type, Class<?>... parameters) throws NoSuchMethodException
	{
		return type.getConstructor(parameters);
	}

	public static Object TryCreateInstance(Constructor<?> constructor, Object... args)
	{
		try {
			return constructor.newInstance(args);
		}
		catch (Exception e) {
			return null;
		}
	}
}
