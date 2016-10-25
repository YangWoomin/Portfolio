using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class SetRankingItem : MonoBehaviour {
    [HideInInspector]
    public int number = 0;
    [HideInInspector]
    public string id = "";
    [HideInInspector]
    public int rankPoint = 0;

    public Text textNumber;
    public Image rankImage;
    public Text textId;
    public Text textRankPoint;

    public void showItem()
    {
        textNumber.text = number.ToString();
        int rankLevel = rankPoint / 100;
        if (rankLevel > 58)
        {
            rankLevel = 58;
        }
        rankImage.GetComponent<Image>().sprite = Resources.Load<Sprite>("Ranking Icon/" + rankLevel);
        id = id.Substring(1, id.Length - 2);
        textId.text = id;
        textRankPoint.text = rankPoint.ToString();
    }
}
