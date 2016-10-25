using UnityEngine;
using System.Collections;

public class GameResult : MonoBehaviour {
    private int state;
    private string item;
    private string itemType;

    public void setState(int value)
    {
        state = value;
    }

    public int getState()
    {
        return state;
    }

    public void setItem(string name)
    {
        item = name;
    }

    public string getItem()
    {
        return item;
    }

    public void setItemType(string type)
    {
        itemType = type;
    }

    public string getItemType()
    {
        return itemType;
    }
}
