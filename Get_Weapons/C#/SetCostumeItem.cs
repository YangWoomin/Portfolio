using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class SetCostumeItem : MonoBehaviour {
    [HideInInspector]
    public string costumeName = "";
    [HideInInspector]
    public int quantity = -1;

    private GameItems gi;

    public Text costumeNameText;
    public Image costumeImage;

    void Awake()
    {
        transform.FindChild("CostumeButton").GetComponent<Image>().color = new Color(0.0f, 0.0f, 0.0f, 0.3f);
    }

    public void OnClickButton()
    {
        gi.setCostumeClicked(costumeName, quantity);
        transform.FindChild("CostumeButton").GetComponent<Image>().color = new Color(0.5f, 0.5f, 0.5f, 0.1f);
    }

    public void showItem()
    {
        gi = GameObject.Find("GameItems(Clone)").GetComponent<GameItems>();
        costumeName = costumeName.Substring(1, costumeName.Length - 2);
        costumeNameText.text = costumeName + "(" + quantity + ")";
        costumeImage.GetComponent<Image>().sprite = Resources.Load<Sprite>("Costume Icon/" + costumeName);
    }

    public string getCostumeItem()
    {
        return costumeName;
    }

    public void setDefault()
    {
        if (string.Equals(costumeName, "Standard"))
        {
            OnClickButton();
        }
    }
}
