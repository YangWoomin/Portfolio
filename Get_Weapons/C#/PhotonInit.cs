using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class PhotonInit : MonoBehaviour {
    public string version = "v1.0";
    private bool joinedLobby;

    private LobyGameManager lgm;

    void Awake()
    {
        PhotonNetwork.ConnectUsingSettings(version);
        PhotonNetwork.isMessageQueueRunning = true;
        joinedLobby = false;

        lgm = GameObject.Find("LobyGameManager").GetComponent<LobyGameManager>();
    }

    void OnJoinedLobby()
    {
        joinedLobby = true;
    }

    void OnPhotonRandomJoinFailed()
    {
        RoomOptions ro = new RoomOptions();
        ro.MaxPlayers = 2;
        PhotonNetwork.CreateRoom(null, ro, TypedLobby.Default);
    }

    void OnJoinedRoom()
    {
        StartCoroutine(this.LoadScene("scBattleField1"));
    }

    IEnumerator LoadScene(string sceneName)
    {
        PhotonNetwork.isMessageQueueRunning = false;
        AsyncOperation ao = SceneManager.LoadSceneAsync(sceneName);
        yield return ao;
    }

    public void OnClickJoinBattleField()
    {
        DontDestroyOnLoad(GameObject.Find("GameItems(Clone)"));
        PhotonNetwork.player.name = PlayerPrefs.GetString("USER_ID");
        PhotonNetwork.JoinRandomRoom();
    }

    void OnGUI()
    {
        GUILayout.Label(PhotonNetwork.connectionStateDetailed.ToString());
    }

    public bool getJoinedLobby()
    {
        return joinedLobby;
    }
}
