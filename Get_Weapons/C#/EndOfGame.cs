using UnityEngine;
using System.Collections;
using UnityEngine.UI;


public class EndOfGame : MonoBehaviour {
    private GameResult gr;

    private GameObject aim;

    private Text resultTitle;
    private Text resultPoint;
    private Text resultItem;

	void Awake () {
        gr = GameObject.Find("GameResult").GetComponent<GameResult>();

        aim = GameObject.Find("Aim");

        resultTitle = transform.FindChild("ResultTitle").GetComponent<Text>();
        resultPoint = transform.FindChild("ResultPoint").GetComponent<Text>();
        resultItem = transform.FindChild("ResultItem").GetComponent<Text>();
    }

    public void setActive(bool value)
    {
        gameObject.SetActive(value);
    }
	
	public void setWin()
    {
        aim.SetActive(false);
        resultTitle.text = "YOU WIN";
        resultTitle.color = new Color(0.0f, 1.0f, 0.0f, 1.0f);
        resultPoint.text = "20";
        resultPoint.color = new Color(0.0f, 1.0f, 0.0f, 1.0f);
        resultItem.color = new Color(0.0f, 1.0f, 0.0f, 1.0f);
        int num = Random.Range(1, 100);
        if(num < 25)
        {
            gr.setItemType("costumes");
            gr.setItem("Snow");
            resultItem.text = "Snow";
        }
        else if(num < 50)
        {
            gr.setItemType("costumes");
            gr.setItem("Dark");
            resultItem.text = "Dark";
        }
        else if(num < 55)
        {
            gr.setItemType("weapons");
            gr.setItem("AssaultMAS");
            resultItem.text = "AssaultMAS";
        }
        else if(num < 65)
        {
            gr.setItemType("weapons");
            gr.setItem("Assault2002");
            resultItem.text = "Assault2002";
        }
        else if(num < 80)
        {
            gr.setItemType("weapons");
            gr.setItem("Assault971");
            resultItem.text = "Assault971";
        }
        else
        {
            gr.setItemType("weapons");
            gr.setItem("Assault-91");
            resultItem.text = "Assault-91";
        }
    }

    public void setDefeat()
    {
        aim.SetActive(false);
        resultTitle.text = "YOU LOSE";
        resultTitle.color = new Color(1.0f, 0.0f, 0.0f, 1.0f);
        resultPoint.text = "-20";
        resultPoint.color = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    }
}
