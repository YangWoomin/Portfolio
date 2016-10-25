using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using UnityEngine.EventSystems;

public class GameItems : MonoBehaviour {
    private string costume;
    private int costumeQuantity;
    private string weapon;
    private int weaponQuantity;

    public void setCostumeClicked(string value, int quantity)
    {
        costume = value;
        costumeQuantity = quantity;

        foreach (GameObject obj in GameObject.FindGameObjectsWithTag("CostumeItem"))
        {
            SetCostumeItem sci = obj.GetComponent<SetCostumeItem>();
            if(!string.Equals(sci.getCostumeItem(), value))
            {
                obj.transform.FindChild("CostumeButton").GetComponent<Image>().color = new Color(0.0f, 0.0f, 0.0f, 0.3f);
            }
        }
    }

    public void setWeaponClicked(string value, int quantity)
    {
        weapon = value;
        weaponQuantity = quantity;

        foreach(GameObject obj in GameObject.FindGameObjectsWithTag("WeaponItem"))
        {
            SetWeaponItem swi = obj.GetComponent<SetWeaponItem>();
            if (!string.Equals(swi.getWeaponItem(), value))
            {
                obj.transform.FindChild("WeaponButton").GetComponent<Image>().color = new Color(0.0f, 0.0f, 0.0f, 0.3f);
            }
        }
    }

    public void setCostume(string value)
    {
        costume = value;
    } 

    public string getCostume()
    {
        return costume;
    }

    public void setWeapon(string value)
    {
        weapon = value;
    }

    public string getWeapon()
    {
        return weapon;
    }

    public int getCostumeQuantity(string value)
    {
        return costumeQuantity;
    }

    public int getWeaponQuantity(string value)
    {
        return weaponQuantity;
    }
}
