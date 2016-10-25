using UnityEngine;
using System.Collections;
using UnityEngine.SceneManagement;
using UnityEngine.UI;
using System;

public class Field1GameManager : MonoBehaviour {
    private PlayerControl pc;
    private UIControl uc;
    private WeaponControl wc;
    private EndOfGame eg;

    private AudioSource _as = null;
    public AudioClip zoomSound;
    public AudioClip retrieveSound;

    private GameObject player;
    private GameObject weapon;
    private GameObject cameraPos;
    private Transform mainCamera;
    private Transform focus;
    private Transform standFirePos;
    private Transform crouchFirePos;
    private Transform standFocus;
    private Transform crouchFocus;
    private Transform swapCameraPos;
    private Transform firePos;

    public Transform weaponPos;
    private bool zoomed;
    private bool gameover;
    private bool swapCamPos;

    private GameObject gameResult;
    private GameResult gr;

    private int maxPlyaerNumber = 2;
    private bool gameStarted;

    private IEnumerator coroutine;

    public Canvas mainCanvas;
    public Canvas loadingCanvas;
    public Canvas quitCanvas;

    public Text waitingText;

    private GameItems gi;
    public GameObject assault74M;
    public GameObject assault91;
    public GameObject assault971;
    public GameObject assault2002;
    public GameObject assaultMAS;

    void Awake () {
        PhotonNetwork.isMessageQueueRunning = true;
        mainCamera = Camera.main.transform;

        _as = GetComponent<AudioSource>();

        setCanvas(0);

        gameResult = GameObject.Find("GameResult");
        gr = gameResult.GetComponent<GameResult>();

        eg = GameObject.Find("EndOfGame").GetComponent<EndOfGame>();
        eg.setActive(false);

        gi = GameObject.Find("GameItems(Clone)").GetComponent<GameItems>();

        gameStarted = false;
        zoomed = false;
        gameover = false;
        swapCamPos = false;

        StartCoroutine(CheckPlayerNumber());
    }

    IEnumerator CheckPlayerNumber()
    {
        bool first = false;
        Room curRoom = PhotonNetwork.room;
        while(true)
        {
            try
            {
                if (curRoom.playerCount == maxPlyaerNumber)
                {
                    break;
                }
                first = true;
            }
            catch(Exception exc) {
                string what = exc.Message;
            }
            yield return new WaitForSeconds(0.5f);
        }
        Vector3 birthPos = new Vector3(-48.99288f, 15.75f, -15.52526f);
        if(!first)
        {
           // birthPos = new Vector3(-123.5668f, 15.75016f, -29.57426f); 
        }
        CreatePlayer(birthPos);
    }

    void OnPhotonPlayerConnected(PhotonPlayer newPlayer)
    {
        Room curRoom = PhotonNetwork.room;
        curRoom.open = false;
    }

    void OnPhotonPlayerDisconnected(PhotonPlayer outPlayer)
    {
        StartCoroutine(WaitForGameResult(1, 6.0f));
    }

    void CreatePlayer(Vector3 birthPos)
    {
        // weapon part 1
        GameObject weaponObj = assault74M;
        string weaponName = gi.getWeapon();
        if (string.Equals(weaponName, "Assault-91"))
        {
            weaponObj = assault91;
        }
        else if (string.Equals(weaponName, "Assault971"))
        {
            weaponObj = assault971;
        }
        else if (string.Equals(weaponName, "Assault2002"))
        {
            weaponObj = assault2002;
        }
        else if (string.Equals(weaponName, "AssaultMAS"))
        {
            weaponObj = assaultMAS;
        }

        // costume part
        string costumeName = gi.getCostume();

        // create character
        string[] param = new string[2];
        param[0] = costumeName;
        param[1] = weaponName;
        player = PhotonNetwork.Instantiate("Player", birthPos, Quaternion.identity, 0, param);
        standFirePos = player.transform.FindChild("StandFirePos");
        crouchFirePos = player.transform.FindChild("CrouchFirePos");
        standFocus = player.transform.FindChild("StandFocus");
        crouchFocus = player.transform.FindChild("CrouchFocus");
        swapCameraPos = player.transform.FindChild("Bip001 R Hand").FindChild("SwapCameraPos");
        cameraPos = new GameObject("CameraPos");
        cameraPos.transform.position = standFirePos.position;
        firePos = standFirePos;
        focus = standFocus;
        pc = player.GetComponent<PlayerControl>();
        uc = player.GetComponent<UIControl>();

        // weapon part 2
        weapon = (GameObject)Instantiate(weaponObj, Vector3.zero, Quaternion.identity);
        wc = weapon.GetComponent<WeaponControl>();
        weapon.transform.SetParent(weaponPos.FindChild(weaponName));
        weapon.transform.position = weaponPos.FindChild(weaponName).position;
        weapon.transform.localPosition = new Vector3(0, 0, 0);
        weapon.transform.localRotation = Quaternion.identity;

        uc.setWeapon(weaponObj.name);
        pc.setWeapon(weaponObj.name);

        gameStarted = true;
        setCanvas(1);
    }

    public bool getGameStarted()
    {
        return gameStarted;
    }

    public void setCameraPos(bool crouch)
    {
        if(crouch)
        {
            firePos = crouchFirePos;
            StartCoroutine(WaitForCrouch());
            wc.Status = 0;
        }
        else
        {
            firePos = standFirePos;
            StartCoroutine(WaitForStand());
        }
    }

    IEnumerator WaitForCrouch()
    {
        swapCamPos = true;
        for(int i = 0; i < 20; i++)
        {
            cameraPos.transform.position = Vector3.Lerp(cameraPos.transform.position, crouchFirePos.position, 0.5f);
            yield return new WaitForSeconds(0.01f);
        }
        swapCamPos = false;
    }

    IEnumerator WaitForStand()
    {
        swapCamPos = true;
        for (int i = 0; i < 20; i++)
        {
            cameraPos.transform.position = Vector3.Lerp(cameraPos.transform.position, standFirePos.position, 0.5f);
            yield return new WaitForSeconds(0.01f);
        }
        swapCamPos = false;
    }

    public void swapFocus(bool crouch)
    {
        if(crouch)
        {
            focus = crouchFocus;
        }
        else
        {
            focus = standFocus;
        }
    }

    public void setZoom(string param)
    {
        if(gameStarted && uc.getActive())
        {
            uc.setActive(false);
            pc.setActable(false);
            pc.Status = 23;
            if (zoomed)
            {
                _as.PlayOneShot(retrieveSound, 1.0f);
                zoomed = false;
                StartCoroutine(CameraFieldOfView(40, 60, 4));
                wc.Status = -1;
            }
            else
            {
                _as.PlayOneShot(zoomSound, 1.0f);
                zoomed = true;
                StartCoroutine(CameraFieldOfView(60, 40, -4));
                wc.Status = 4;
            }
        }
    }

    public bool getZoomed()
    {
        return zoomed;
    }

    public void setGameover()
    {
        gameover = true;
    }

    IEnumerator CameraFieldOfView(int start, int finish, int offset)
    {
        while(start != finish)
        {
            start += offset;
            mainCamera.GetComponent<Camera>().fieldOfView = start;
            yield return new WaitForSeconds(0.01f);
        }
        uc.setActive(true);
        if(!uc.getCrouch())
        {
            pc.setActable(true);
        }
    }

    public void setCanvas(int num)
    {
        switch(num)
        {
            case 0:
                loadingCanvas.GetComponent<Canvas>().enabled = true;
                mainCanvas.GetComponent<Canvas>().enabled = false;
                quitCanvas.GetComponent<Canvas>().enabled = false;

                coroutine = WaitForAnotherPlayer();
                StartCoroutine(coroutine);
                break;

            case 1:
                loadingCanvas.GetComponent<Canvas>().enabled = false;
                mainCanvas.GetComponent<Canvas>().enabled = true;
                quitCanvas.GetComponent<Canvas>().enabled = false;

                if(coroutine != null)
                {
                    StopCoroutine(coroutine);
                }
                break;

            case 2:
                loadingCanvas.GetComponent<Canvas>().enabled = false;
                mainCanvas.GetComponent<Canvas>().enabled = false;
                quitCanvas.GetComponent<Canvas>().enabled = true;
                break;
        }
    }

    IEnumerator WaitForAnotherPlayer()
    {
        while(true)
        {
            waitingText.text = "Waiting for the other player.";
            yield return new WaitForSeconds(1.0f);
            waitingText.text = "Waiting for the other player..";
            yield return new WaitForSeconds(1.0f);
            waitingText.text = "Waiting for the other player...";
            yield return new WaitForSeconds(1.0f);
            waitingText.text = "Waiting for the other player....";
            yield return new WaitForSeconds(1.0f);
        }
    }

    void LateUpdate()
    {
        if(gameStarted)
        {
            if (Application.platform == RuntimePlatform.Android)
            {
                if (Input.GetKey(KeyCode.Escape))
                {
                    pc.setGameover(true);
                }
            }

            if (pc.getGameover() || gameover)
            {
                // switch canvas
                
                StartCoroutine(WaitForGameResult(2, 3.0f));
            }
            else
            {
                if(!swapCamPos)
                {
                    // set camera position
                    cameraPos.transform.position = Vector3.Lerp(cameraPos.transform.position, firePos.position, 0.5f);
                }
                
                // set camera
                mainCamera.position = Vector3.Lerp(mainCamera.position, cameraPos.transform.position, 0.5f);
                mainCamera.LookAt(focus.position);

                // set weapon
                weaponPos.position = Vector3.Lerp(weaponPos.position, cameraPos.transform.position, 0.5f);
                weaponPos.LookAt(focus.position);
            }
        }
        else
        {
            if (Application.platform == RuntimePlatform.Android)
            {
                if (Input.GetKey(KeyCode.Escape))
                {
                    StartCoroutine(WaitForGameResult(0, 0.0f));
                }
            }
        }
    }

    IEnumerator WaitForGameResult(int result, float delay)
    {
        eg.setActive(true);
        if(result == 1)
        {
            eg.setWin();
        }
        else if(result == 2)
        {
            eg.setDefeat();
        }
        yield return new WaitForSeconds(delay);

        gr.setState(result);
        DontDestroyOnLoad(gameResult);

        PhotonNetwork.LeaveRoom();
    }

    void OnLeftRoom()
    {
        StartCoroutine(LoadScene("scLoby"));
    }

    IEnumerator LoadScene(string sceneName)
    {
        PhotonNetwork.isMessageQueueRunning = false;
        AsyncOperation ao = SceneManager.LoadSceneAsync(sceneName);
        yield return ao;
    }
}
