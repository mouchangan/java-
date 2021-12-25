
import java.util.*;
//���ͼ����࣬�������������ӡ���Ϸ�������Ϣ��
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

	//V��һ����������ռλ�����ǳ��������βΡ�����������������������������
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