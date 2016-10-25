using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Collections.Generic;
using UnityEngine.SceneManagement;
using System;

public class LobyGameManager : MonoBehaviour {
    private PhotonInit pi;
    private GameItems gi;
    public GameObject gogi;

    public GameObject mainCanvas;
    public GameObject createAccountCanvas;
    public GameObject introCanvas;
    public InputField newId;
    public Text messageText;
    public Text userId;
    public Text userRP;
    public Image rankImage;

    private int dbWorks;
    private int defaultDBWorks;

    // ranking
    public GameObject rankingScrollContents;
    public GameObject rankingScrollbar;
    private int scrollPos;
    private int itemNumber;
    public GameObject userInfoItem;

    // costumes
    public GameObject costumeScrollContents;
    public GameObject costumeScrollbar;
    public GameObject costumeInfoItem;

    // weapons
    public GameObject weaponScrollContents;
    public GameObject weaponScrollbar;
    public GameObject weaponInfoItem;

    private GameObject gameResult;
    private GameResult gr;
    private string id;

    private IEnumerator loadingScene;

    void Start()
    {
        pi = GameObject.Find("PhotonInit").GetComponent<PhotonInit>();
        

        DataManager.instance.OnHttpRequestForCreatingAccount += OnHttpRequestForCreatingAccount;
        DataManager.instance.OnHttpRequestForRankingList += OnHttpRequestForRankingList;
        DataManager.instance.OnHttpRequestForUpdateRankingPoint += OnHttpRequestForUpdateRankingPoint;
        DataManager.instance.OnHttpRequestForCostumes += OnHttpRequestForCostumes;
        DataManager.instance.OnHttpRequestForWeapons += OnHttpRequestForWeapons;

        DataManager.instance.OnHttpRequestForDML += OnHttpRequestForDML;

        dbWorks = 0;
        defaultDBWorks = 0;

        loadingScene = LoadingScene();
        StartCoroutine(loadingScene);

        id = PlayerPrefs.GetString("USER_ID");
        scrollPos = 0;
        itemNumber = 1;

        gameResult = GameObject.Find("GameResult");
        if (gameResult != null)
        {
            gr = gameResult.GetComponent<GameResult>();
            gi = GameObject.Find("GameItems(Clone)").GetComponent<GameItems>();
            int state = gr.getState();
            int point = 0;
            switch(state)
            {
                case 0:
                    point = 0;
                    break;
                case 1:
                    point = 20;
                    string itemType = gr.getItemType();
                    string item = gr.getItem();
                    updateGottenItem();

                    if(gi != null)
                    {
                        updateCostume();
                        updateWeapons();
                    }
                    break;
                case 2:
                    point = -20;

                    if (gi != null)
                    {
                        updateCostume();
                        updateWeapons();
                    }
                    break;
				case 5:
					setCanvas (0);
					break;
                default:
                    point = 0;
                    break;
            }
            if(point != 0)
            {
                dbWorks++;
                DataManager.instance.UpdateRankingPoint(id, point);
            }
            Destroy(gameResult);
        }
        else
        {
            GameObject gameItems = (GameObject)Instantiate(gogi);
            gi = gogi.GetComponent<GameItems>();
            if (string.IsNullOrEmpty(PlayerPrefs.GetString("USER_ID")))
            {
                StartCoroutine(WaitForPhoton(1));
                return;
            }
        }
        StartCoroutine(WaitForDBWorks());
    }

    public void setCanvas(int value)
    {
        switch(value)
        {
            case 0:
                mainCanvas.SetActive(true);
                createAccountCanvas.SetActive(false);
                introCanvas.SetActive(false);
                rankingScrollbar.GetComponent<Scrollbar>().value = scrollPos / (float)itemNumber;
                break;

            case 1:
                mainCanvas.SetActive(false);
                createAccountCanvas.SetActive(true);
                introCanvas.SetActive(false);
                break;

            case 2:
                mainCanvas.SetActive(false);
                createAccountCanvas.SetActive(false);
                introCanvas.SetActive(true);
                break;
        }
    }

    IEnumerator LoadingScene()
    {
        setCanvas(2);
        while (true)
        {
            yield return new WaitForSeconds(1.0f);
        }
    }

    IEnumerator WaitForDBWorks()
    {
        while(dbWorks != 0)
        {
            yield return new WaitForSeconds(1.0f);
        }
        defaultDBWorks += 3;
        DataManager.instance.GetRankingList(id);
        DataManager.instance.GetCostumes(id);
        DataManager.instance.GetWeapons(id);
        StartCoroutine(WaitForDefaultDBWorks());
    }

    IEnumerator WaitForDefaultDBWorks()
    {
        while(defaultDBWorks != 0)
        {
            yield return new WaitForSeconds(1.0f);
        }
        StartCoroutine(WaitForPhoton(0));
    }

    IEnumerator WaitForPhoton(int value)
    {
        while(!pi.getJoinedLobby())
        {
            yield return new WaitForSeconds(1.0f);
        }
        yield return new WaitForSeconds(2.0f);

        try
        {
            StopCoroutine(loadingScene);
            setCanvas(value);
        }
        catch(Exception exc)
        {
            Debug.Log(exc.Message);
        }
    }

    public void OnHttpRequestForCreatingAccount(int result)
    {
        if (result == 0)
        {
            dbWorks--;
            PlayerPrefs.SetString("USER_ID", newId.text);
            id = newId.text;
        }
        else if (result == -2)
        {
            messageText.color = Color.red;
            messageText.text = "Duplicate ID";
            return;
        }
        else
        {
            messageText.color = Color.yellow;
            messageText.text = "Check your network ";
            return;
        }
    }

    public void OnHttpRequestForRankingList(string[] ids, int[] rank_points, int[] rankings)
    {
        foreach(GameObject obj in GameObject.FindGameObjectsWithTag("RankingListItem"))
        {
            Destroy(obj);
        }

        int rowCount = 0;
        rankingScrollContents.GetComponent<RectTransform>().sizeDelta = Vector2.zero;
        for(int i = 0; i < ids.Length; i++)
        {
            GameObject infoItem = (GameObject)Instantiate(userInfoItem);
            infoItem.transform.SetParent(rankingScrollContents.transform, false);
            SetRankingItem setRankingItem = infoItem.GetComponent<SetRankingItem>();
            setRankingItem.number = rankings[i];
            setRankingItem.id = ids[i];
            setRankingItem.rankPoint = rank_points[i];
            setRankingItem.showItem();
            rankingScrollContents.GetComponent<RectTransform>().pivot = new Vector2(0.0f, 1.0f);
            rankingScrollContents.GetComponent<GridLayoutGroup>().constraintCount = ++rowCount;
            rankingScrollContents.GetComponent<RectTransform>().sizeDelta += new Vector2(0, 82);
            if(string.Equals(setRankingItem.id, id))
            {
                infoItem.GetComponent<Image>().color = Color.gray;
                infoItem.transform.FindChild("YourRanking").gameObject.GetComponent<Text>().enabled = true;
                scrollPos = i;
            }
        }
        itemNumber = ids.Length;
        defaultDBWorks--;
    }

    public void OnHttpRequestForUpdateRankingPoint(int result)
    {
        if(result == 0)
        {
            dbWorks--;
        }
    }

    public void OnHttpRequestForCostumes(string[] costumes, int[] quantities)
    {
        foreach (GameObject obj in GameObject.FindGameObjectsWithTag("CostumeItem"))
        {
            Destroy(obj);
        }

        int colCount = 0;
        costumeScrollContents.GetComponent<RectTransform>().sizeDelta = Vector2.zero;
        for(int i = 0; i < costumes.Length; i++)
        {
            GameObject infoItem = (GameObject)Instantiate(costumeInfoItem);
            infoItem.transform.SetParent(costumeScrollContents.transform, false);
            SetCostumeItem setCostumeItem = infoItem.GetComponent<SetCostumeItem>();
            setCostumeItem.costumeName = costumes[i];
            setCostumeItem.quantity = quantities[i];
            setCostumeItem.showItem();
            setCostumeItem.setDefault();
            costumeScrollContents.GetComponent<RectTransform>().pivot = new Vector2(0.0f, 1.0f);
            costumeScrollContents.GetComponent<GridLayoutGroup>().constraintCount = ++colCount;
            costumeScrollContents.GetComponent<RectTransform>().sizeDelta += new Vector2(120, 0);
        }
        defaultDBWorks--;
    }

    public void OnHttpRequestForWeapons(string[] weapons, int[] quantities)
    {
        foreach (GameObject obj in GameObject.FindGameObjectsWithTag("WeaponItem"))
        {
            Destroy(obj);
        }

        int colCount = 0;
        weaponScrollContents.GetComponent<RectTransform>().sizeDelta = Vector2.zero;
        for (int i = 0; i < weapons.Length; i++)
        {
            GameObject infoItem = (GameObject)Instantiate(weaponInfoItem);
            infoItem.transform.SetParent(weaponScrollContents.transform, false);
            SetWeaponItem setWeaponItem = infoItem.GetComponent<SetWeaponItem>();
            setWeaponItem.weaponName = weapons[i];
            setWeaponItem.quantity = quantities[i];
            setWeaponItem.showItem();
            setWeaponItem.setDefault();
            weaponScrollContents.GetComponent<RectTransform>().pivot = new Vector2(0.0f, 1.0f);
            weaponScrollContents.GetComponent<GridLayoutGroup>().constraintCount = ++colCount;
            weaponScrollContents.GetComponent<RectTransform>().sizeDelta += new Vector2(172, 0);
        }
        defaultDBWorks--;
    }

    public void OnClickCreateAccount()
    {
        if (string.IsNullOrEmpty(newId.text))
        {
            messageText.color = Color.red;
            messageText.text = "Input new id";
            return;
        }
        else if(newId.text.Length > 8)
        {
            messageText.color = Color.red;
            messageText.text = "Maximum length : 8";
            return;
        }
        dbWorks++;
        DataManager.instance.CreateAccount(newId.text);
        StartCoroutine(WaitForDBWorks());
    }

    public void OnClickTutorialScene()
    {
        StartCoroutine(this.LoadScene("scTutorial2"));
    }

    IEnumerator LoadScene(string sceneName)
    {
        PhotonNetwork.isMessageQueueRunning = false;
        AsyncOperation ao = SceneManager.LoadSceneAsync(sceneName);
        yield return ao;
    }

    public void updateCostume()
    {
        string costumeName = gi.getCostume();
        if(string.Equals(costumeName, "Standard"))
        {
            return;
        }
        else
        {
            dbWorks++;
            DataManager.instance.SetItemQuantity("costumes", id, costumeName, gi.getCostumeQuantity(costumeName));
        }
    }

    public void updateWeapons()
    {
        string weaponName = gi.getWeapon();
        if(string.Equals(weaponName, "Assault74M"))
        {
            return;
        }
        else
        {
            dbWorks++;
            DataManager.instance.SetItemQuantity("weapons", id, weaponName, gi.getWeaponQuantity(weaponName));
        }
    }

    public void updateGottenItem()
    {
        string itemType = gr.getItemType();
        dbWorks++;
        DataManager.instance.SetGottenItem(itemType, id, gr.getItem());
    }

    public void OnHttpRequestForDML(int result)
    {
        if(result == 0)
        {
            dbWorks--;
        }
    }

    void Update () {
        if (Application.platform == RuntimePlatform.Android)
        {
            if (Input.GetKey(KeyCode.Escape))
            {
                Application.Quit();
            }
        }
    }
}
