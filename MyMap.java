
import java.util.*;
//泛型集合类，后续用来存桌子、游戏中玩家信息等
public class MyMap<K,V> extends Hashtable<K,V>
{	
	public void removeByValue(Object value) 
	{
		for (Object key : keySet())
		{
			if (get(key) == value)
			{
				remove(key);
				return ;
			}
		}
	}

	//V是一个数据类型占位符，非常类似于形参。。。。。。。。。。。。。。。
	public Set<V> valueSet() 
	{
		Set<V> result = new HashSet<V>();
		//
		for (K key : keySet())
		{
			result.add(get(key));
		}
		return result;
	}
}