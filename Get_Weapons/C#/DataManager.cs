using UnityEngine;
using System.Collections;
using SimpleJSON;
using System.Collections.Generic;

public class DataManager : MonoBehaviour
{
    public static DataManager instance = null;

    public delegate void HttpRequestForCreatingAccountDelegate(int result);
    public event HttpRequestForCreatingAccountDelegate OnHttpRequestForCreatingAccount;
    public delegate void HttpRequestForGettingRankingPoint(int result);
    public event HttpRequestForGettingRankingPoint OnHttpRequestForGettingRankingPoint;
    public delegate void HttpRequestForRankingListDelegate(string[] id, int[] rank_point, int[] ranking);
    public event HttpRequestForRankingListDelegate OnHttpRequestForRankingList;
    public delegate void HttpRequestForUpdateRankingPointDelegate(int result);
    public event HttpRequestForUpdateRankingPointDelegate OnHttpRequestForUpdateRankingPoint;
    public delegate void HttpRequestForCostumes(string[] items, int[] quantities);
    public event HttpRequestForCostumes OnHttpRequestForCostumes;
    public delegate void HttpRequestForWeapons(string[] items, int[] quantities);
    public event HttpRequestForWeapons OnHttpRequestForWeapons;

    public delegate void HttpRequestForDMLDelegate(int result);
    public event HttpRequestForDMLDelegate OnHttpRequestForDML;

    private string url1 = "http://192.168.168.101";
    private string createAccountUrl;
    private string loginUrl;
    private string rankingUrl;
    private string updateRankingPointUrl;
    private string costumesUrl;
    private string weaponsUrl;
    private string gottenItemUrl;
    private string dmlUrl;
    private string selUrl;

    void Awake()
    {
        instance = this;
        createAccountUrl = url1 + "/GW_create_account.php";
        loginUrl = url1 + "/GW_get_ranking_point.php";
        rankingUrl = url1 + "/GW_get_ranking_list.php";
        updateRankingPointUrl = url1 + "/GW_update_ranking_point.php";
        costumesUrl = url1 + "/GW_get_costumes.php";
        weaponsUrl = url1 + "/GW_get_weapons.php";
        gottenItemUrl = url1 + "/GW_gotten_item.php";
        dmlUrl = url1 + "/GW_DML.php";
        selUrl = url1 + "/GW_SEL.php";
    } 

    public void CreateAccount(string id)
    {
        WWWForm form = new WWWForm();
        form.AddField("id", id);
        var www = new WWW(createAccountUrl, form);
        StartCoroutine(WaitForRequestCreatingAccount(www));
    }

    public IEnumerator WaitForRequestCreatingAccount(WWW www)
    {
        yield return www;
        // 응답이 왔다면, 이벤트 리스너에 응답 결과 전달
        bool hasCompleteListener;
        hasCompleteListener = (OnHttpRequestForCreatingAccount != null);

        if (hasCompleteListener)
        {
            int result = -1;
            if (string.IsNullOrEmpty(www.error))
            {
                string temp = www.text;
                var N = JSON.Parse(temp);
                string res = N["status"].ToString();
                if (string.Compare("\"OK\"", res, true) == 0)
                {
                    result = 0;
                }
                else if (string.Compare("\"NO\"", res, true) == 0)
                {
                    result = -2;
                }
                else
                {
                    result = -1;
                }
            }
            else
            {
                result = -1;
            }
            OnHttpRequestForCreatingAccount(result);
        }

        // 통신 해제
        www.Dispose();
    }

    public void GetRankingPoint(string id)
    {
        WWWForm form = new WWWForm();
        form.AddField("id", id);
        var www = new WWW(loginUrl, form);
        StartCoroutine(WaitForRequestGettingRankingPoint(www));
    }

    public IEnumerator WaitForRequestGettingRankingPoint(WWW www)
    {
        yield return www;
        // 응답이 왔다면, 이벤트 리스너에 응답 결과 전달
        bool hasCompleteListener = (OnHttpRequestForGettingRankingPoint != null);

        if (hasCompleteListener)
        {
            int result = -1;
            if (string.IsNullOrEmpty(www.error))
            {
                string temp = www.text;
                var N = JSON.Parse(temp);
                result = N["rank_point"].AsInt;
            }
            else
            {
                result = -1;
            }
            OnHttpRequestForGettingRankingPoint(result);
        }

        // 통신 해제
        www.Dispose();
    }

    public void GetRankingList(string id)
    {
        WWWForm form = new WWWForm();
        form.AddField("id", id);
        var www = new WWW(rankingUrl, form);
        StartCoroutine(WaitForRequestRankingList(www));
    }

    public IEnumerator WaitForRequestRankingList(WWW www)
    {
        yield return www;
        bool hasCompleteListener = (OnHttpRequestForRankingList != null);
        if(hasCompleteListener)
        {
            if (string.IsNullOrEmpty(www.error))
            {
                string temp = www.text;
                var N = JSON.Parse(temp);
                string[] ids = new string[N.Count];
                int[] rank_points = new int[N.Count];
                int[] rankings = new int[N.Count];

                for(int i = 0; i < N.Count; i++)
                {
                    ids[i] = N[i]["id"].ToString();
                    rank_points[i] = N[i]["rank_point"].AsInt;
                    rankings[i] = N[i]["ranking"].AsInt;
                }
                OnHttpRequestForRankingList(ids, rank_points, rankings);
            }
            else
            {
                Debug.Log(www.error);
            }
        }
        www.Dispose();
    }

    public void UpdateRankingPoint(string id, int point)
    {
        WWWForm form = new WWWForm();
        form.AddField("id", id);
        form.AddField("point", point);
        var www = new WWW(updateRankingPointUrl, form);
        StartCoroutine(WaitForRequestUpdateRankingPoint(www));
    }

    public IEnumerator WaitForRequestUpdateRankingPoint(WWW www)
    {
        yield return www;
        // 응답이 왔다면, 이벤트 리스너에 응답 결과 전달
        bool hasCompleteListener;
        hasCompleteListener = (OnHttpRequestForUpdateRankingPoint != null);

        if (hasCompleteListener)
        {
            int result = -1;
            if (string.IsNullOrEmpty(www.error))
            {
                string temp = www.text;
                var N = JSON.Parse(temp);
                string res = N["status"].ToString();
                if (string.Compare("\"OK\"", res, true) == 0)
                {
                    result = 0;
                }
                else if (string.Compare("\"NO\"", res, true) == 0)
                {
                    result = -2;
                }
                else
                {
                    result = -1;
                }
            }
            else
            {
                result = -1;
                Debug.Log(www.error);
            }
            OnHttpRequestForUpdateRankingPoint(result);
        }

        // 통신 해제
        www.Dispose();
    }

    public void GetCostumes(string id)
    {
        WWWForm form = new WWWForm();
        form.AddField("id", id);
        var www = new WWW(costumesUrl, form);
        StartCoroutine(WaitForRequestCostumes(www));
    }

    public IEnumerator WaitForRequestCostumes(WWW www)
    {
        yield return www;
        bool hasCompleteListener = (OnHttpRequestForCostumes != null);
        if (hasCompleteListener)
        {
            if (string.IsNullOrEmpty(www.error))
            {
                string temp = www.text;
                var N = JSON.Parse(temp);
                string[] costumes = new string[N.Count];
                int[] quantities = new int[N.Count];

                for (int i = 0; i < N.Count; i++)
                {
                    costumes[i] = N[i]["item_name"].ToString();
                    quantities[i] = N[i]["quantity"].AsInt;
                }
                OnHttpRequestForCostumes(costumes, quantities);
            }
            else
            {
                Debug.Log(www.error);
            }
        }
        www.Dispose();
    }

    public void GetWeapons(string id)
    {
        WWWForm form = new WWWForm();
        form.AddField("id", id);
        var www = new WWW(weaponsUrl, form);
        StartCoroutine(WaitForRequestWeapons(www));
    }

    public IEnumerator WaitForRequestWeapons(WWW www)
    {
        yield return www;
        bool hasCompleteListener = (OnHttpRequestForWeapons != null);
        if (hasCompleteListener)
        {
            if (string.IsNullOrEmpty(www.error))
            {
                string temp = www.text;
                var N = JSON.Parse(temp);
                string[] weapons = new string[N.Count];
                int[] quantities = new int[N.Count];

                for (int i = 0; i < N.Count; i++)
                {
                    weapons[i] = N[i]["item_name"].ToString();
                    quantities[i] = N[i]["quantity"].AsInt;
                }
                OnHttpRequestForWeapons(weapons, quantities);
            }
            else
            {
                Debug.Log(www.error);
            }
        }
        www.Dispose();
    }

    public void SetItemQuantity(string table, string id, string itemName, int quantity)
    {
        string query = "";
        if(quantity > 1)
        {
            query = "update " + table + " set quantity = " + (--quantity) + " where id = '" + id + "' and item_name = '" + itemName + "'";
        }
        else
        {
            query = "delete from " + table + " where id = '" + id + "' and item_name = '" + itemName + "'";
        }
        WWWForm form = new WWWForm();
        form.AddField("query", query);
        var www = new WWW(dmlUrl, form);
        StartCoroutine(WaitForRequestDML(www));
    }

    public void SetGottenItem(string table, string id, string itemName)
    {
        WWWForm form = new WWWForm();
        form.AddField("table", table);
        form.AddField("id", id);
        form.AddField("item_name", itemName);
        var www = new WWW(gottenItemUrl, form);
        StartCoroutine(WaitForRequestDML(www));
    }

    public IEnumerator WaitForRequestDML(WWW www)
    {
        yield return www;
        bool hasCompleteListener;
        hasCompleteListener = (OnHttpRequestForDML != null);

        if (hasCompleteListener)
        {
            int result = -1;
            if (string.IsNullOrEmpty(www.error))
            {
                string temp = www.text;
                var N = JSON.Parse(temp);
                string res = N["status"].ToString();
                if (string.Compare("\"OK\"", res, true) == 0)
                {
                    result = 0;
                }
                else if (string.Compare("\"NO\"", res, true) == 0)
                {
                    result = -2;
                }
                else
                {
                    result = -1;
                }
            }
            else
            {
                result = -1;
                Debug.Log(www.error);
            }
            OnHttpRequestForDML(result);
        }

        // 통신 해제
        www.Dispose();
    }
}
