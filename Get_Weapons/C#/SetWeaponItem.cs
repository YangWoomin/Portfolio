using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class SetWeaponItem : MonoBehaviour {
    [HideInInspector]
    public string weaponName = "";
    [HideInInspector]
    public int quantity = -1;

    private GameItems gi;

    public Text weaponNameText;
    public Image weaponImage;

    void Awake()
    {
        
        transform.FindChild("WeaponButton").GetComponent<Image>().color = new Color(0.0f, 0.0f, 0.0f, 0.3f);
    }

    public void showItem()
    {
        gi = GameObject.Find("GameItems(Clone)").GetComponent<GameItems>();
        weaponName = weaponName.Substring(1, weaponName.Length - 2);
        weaponNameText.text = weaponName + "(" + quantity + ")";
        weaponImage.GetComponent<Image>().sprite = Resources.Load<Sprite>("Weapon Icon/" + weaponName);
    }

    public void OnClickButton()
    {
        gi.setWeaponClicked(weaponName, quantity);
        transform.FindChild("WeaponButton").GetComponent<Image>().color = new Color(0.5f, 0.5f, 0.5f, 0.1f);
    }

    public string getWeaponItem()
    {
        return weaponName;
    }

    public void setDefault()
    {
        if (string.Equals(weaponName, "Assault74M"))
        {
            OnClickButton();
        }
    }
}
