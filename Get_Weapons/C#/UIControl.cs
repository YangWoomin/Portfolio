using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;
using UnityEngine.UI;
using System;

public class UIControl : MonoBehaviour {
    private PhotonView pv = null;
    private PlayerControl pc;
    private PlayerRotation pr;
    private Field1GameManager f1g;
    private WeaponControl wc;

    

    private GameObject weaponsCrate;

    private GameObject fireButton;
    private GameObject crouchButton;
    private GameObject reloadButton;
    private GameObject zoomButton;
    private Transform firePos;
    private Transform standFirePos;
    private Transform crouchFirePos;
    private Transform focus;
    private Transform standFocus;
    private Transform crouchFocus;
    private RawImage crouchImage;
    private RawImage standImage;

    private RawImage aimLeft;
    private RawImage aimRight;
    private RawImage aimTop;
    private RawImage aimBottom;

    private Gyroscope gyro;

    private RaycastHit hit;

    private int health;
    private int bodyDamage;
    private int branchDamage;
    private bool crouched;
    private bool active;
    private int curBullets;
    private int totalBullets;

    public GameObject bloodEffect;
    public GameObject bloodDecal;
    private Vector3 damagedPos = Vector3.zero;
    private IEnumerator waitForDamaged;

    // player status
    private Image hpBar;
    private Text hpText;
    private Text hpPoint;
    private Text curBullet;
    private Text totalBullet;

    // get crate
    private GameObject getCratePanel;
    private Image getCrateBar;
    private Text getCratePercent;

    private AndroidJavaClass ajc;
    private AndroidJavaObject ajo;

    void Awake () {
        pv = GetComponent<PhotonView>();
        pc = GetComponent<PlayerControl>();
        pr = GetComponent<PlayerRotation>();
        f1g = GameObject.Find("Field1GameManager").GetComponent<Field1GameManager>();

        // gyroscope sensor
        gyro = Input.gyro;
        gyro.enabled = true;

        standFirePos = transform.FindChild("StandFirePos");
        crouchFirePos = transform.FindChild("CrouchFirePos");
        firePos = standFirePos;

        standFocus = transform.FindChild("StandFocus");
        crouchFocus = transform.FindChild("CrouchFocus");
        focus = standFocus;

        health = 100;
        crouched = false;
        active = true;
        bodyDamage = 20;
        branchDamage = 10;
        curBullets = 30;
        totalBullets = 60;

        if (pv.isMine)
        {
            // fire button event
            fireButton = GameObject.Find("FireButton") as GameObject;
            EventTrigger trigger1 = fireButton.GetComponent<EventTrigger>();

            EventTrigger.Entry onFirePointerDown = new EventTrigger.Entry();
            onFirePointerDown.eventID = EventTriggerType.PointerDown;
            onFirePointerDown.callback.AddListener(delegate { OnFirePointerDownDelegate(); });
            trigger1.triggers.Add(onFirePointerDown);

            EventTrigger.Entry onFirePointerUp = new EventTrigger.Entry();
            onFirePointerUp.eventID = EventTriggerType.PointerUp;
            onFirePointerUp.callback.AddListener(delegate { OnFirePointerUpDelegate(); });
            trigger1.triggers.Add(onFirePointerUp);

            // crouch button event
            crouchButton = GameObject.Find("CrouchButton") as GameObject;
            EventTrigger trigger2 = crouchButton.GetComponent<EventTrigger>();

            EventTrigger.Entry onCrouchPointerClick = new EventTrigger.Entry();
            onCrouchPointerClick.eventID = EventTriggerType.PointerClick;
            onCrouchPointerClick.callback.AddListener(delegate { OnCrouchPointerClickDelegate(); });
            trigger2.triggers.Add(onCrouchPointerClick);

            //// reload button event
            //reloadButton = GameObject.Find("ReloadButton") as GameObject;
            //EventTrigger trigger3 = reloadButton.GetComponent<EventTrigger>();

            //EventTrigger.Entry onReloadPointerClick = new EventTrigger.Entry();
            //onReloadPointerClick.eventID = EventTriggerType.PointerClick;
            //onReloadPointerClick.callback.AddListener(delegate { OnReloadPointerClickDelegate(); });
            //trigger3.triggers.Add(onReloadPointerClick);

            // zoom button event
            //zoomButton = GameObject.Find("ZoomButton") as GameObject;
            //EventTrigger trigger4 = zoomButton.GetComponent<EventTrigger>();

            //EventTrigger.Entry onZoomPointerClick = new EventTrigger.Entry();
            //onZoomPointerClick.eventID = EventTriggerType.PointerClick;
            //onZoomPointerClick.callback.AddListener(delegate { OnZoomPointerClickDelegate(); });
            //trigger4.triggers.Add(onZoomPointerClick);

            // set crouch button swappable with stand and crouch image
            crouchImage = crouchButton.transform.FindChild("CrouchImage").gameObject.GetComponent<RawImage>();
            standImage = crouchButton.transform.FindChild("StandImage").gameObject.GetComponent<RawImage>();
            crouchImage.enabled = true;
            standImage.enabled = false;

            // for aim effect
            aimLeft = GameObject.Find("AimImage1").GetComponent<RawImage>();
            aimRight = GameObject.Find("AimImage2").GetComponent<RawImage>();
            aimTop = GameObject.Find("AimImage3").GetComponent<RawImage>();
            aimBottom = GameObject.Find("AimImage4").GetComponent<RawImage>();

            GameObject go = GameObject.Find("HPImage");
            if(go != null)
            {
                hpBar = go.GetComponent<Image>();
            }
            
            hpBar.color = Color.green;

            go = GameObject.Find("HPText");
            if(go != null)
            {
                hpText = go.GetComponent<Text>();
            }
            go = GameObject.Find("PointText");
            if (go != null)
            {
                hpPoint = go.GetComponent<Text>();
            }

            go = GameObject.Find("CurBullet");
            if(go != null)
            {
                curBullet = go.GetComponent<Text>();
            }

            go = GameObject.Find("TotalBullet");
            if(go != null)
            {
                totalBullet = go.GetComponent<Text>();
            }

            curBullet.text = curBullets.ToString();
            totalBullet.text = " / " + totalBullets.ToString();

            weaponsCrate = GameObject.Find("WeaponsCrate");

            getCratePanel = GameObject.Find("GetCrate");

            go = GameObject.Find("GetCrateBar");
            if(go != null)
            {
                getCrateBar = go.GetComponent<Image>();
            }

            go = GameObject.Find("GetCratePercent");
            if(go != null)
            {
                getCratePercent = go.GetComponent<Text>();
            }

            getCrateBar.color = new Color(2.0f, 2.0f, 2.0f, 0.4f);
            getCrateBar.fillAmount = 0.0f;
            getCratePanel.SetActive(false);
            StartCoroutine(WaitForCrate());

            try
            {
                ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
                ajo = ajc.GetStatic<AndroidJavaObject>("currentActivity");

                if (ajo.Call<int>("StartProximityService") != 0)
                {
                    Debug.Log("native failed");
                }
            }
            catch (Exception exc)
            {
                Debug.Log(exc.Message);
            }
        }
        else
        {
            pc.setDefaultSetting();
        }
    }

    public void setDefaultSetting(int body, int branch)
    {
        bodyDamage = body;
        branchDamage = branch;
    }

    public void setWeapon(string weaponName)
    {
        wc = GameObject.Find(weaponName+"(Clone)").GetComponent<WeaponControl>();
    }

    public bool getCrouch()
    {
        return crouched;
    }

    public bool getActive()
    {
        return active;
    }

    public void setActive(bool value)
    {
        active = value;
    }

    public void setFocus(float value)
    {
        Vector3 vec = new Vector3(standFocus.position.x - standFirePos.position.x,
            standFocus.position.y - standFirePos.position.y,
            standFocus.position.z - standFirePos.position.z);
        float angle = Vector3.Angle(gameObject.GetComponent<Transform>().forward, vec);
        if (standFocus.position.y - standFirePos.position.y >= 0)
        {
            if (angle + value <= 20.0f)
            {
                standFocus.RotateAround(standFirePos.position, -gameObject.GetComponent<Transform>().right, value);
                crouchFocus.RotateAround(crouchFirePos.position, -gameObject.GetComponent<Transform>().right, value);
            }
        }
        else
        {
            if(angle - value <= 20.0f)
            {
                standFocus.RotateAround(standFirePos.position, -gameObject.GetComponent<Transform>().right, value);
                crouchFocus.RotateAround(crouchFirePos.position, -gameObject.GetComponent<Transform>().right, value);
            }
        }
    }

    /*
     * event trigger delegates
     */
    public void OnFirePointerDownDelegate()
    {
        if(active)
        {
            active = false;
            if (crouched)
            {
                pc.Status = 18;
            }
            else
            {
                pc.Status = 10;
            }
        }
    }

    public void OnFirePointerUpDelegate()
    {
        pc.Status = 22;
    }

    public void OnCrouchPointerClickDelegate()
    {
        if(active)
        {
            if(crouched)
            {
                pc.Status = 21;
            }
            else
            {
                pc.Status = 16;
            }
        }
    }

    public void OnReloadPointerClickDelegate()
    {
        doReload();
    }

    
    public void OnZoomPointerClickDelegate()
    {
        Zoom("");
    }

    /*
     * fire
    */
    public void raycastHit(int mult)
    {
        object[] _params = new object[3]; 															// except for layer named CapsuleCollider
        if (Physics.Raycast(firePos.position, focus.position - firePos.position, out hit, 200.0f, (-1) - (1 << LayerMask.NameToLayer("CapsuleCollider"))))
        {
            if (hit.collider.tag == "Player_head")
            {
                _params[0] = "Player_head";
                _params[1] = 100;
            }
            else if (hit.collider.tag == "Player_body")
            {
                _params[0] = "Player_body";
                _params[1] = bodyDamage;
            }
            else if (hit.collider.tag == "Player_branch")
            {
                _params[0] = "Player_branch";
                _params[1] = branchDamage;
            }
            // Addition *******************************************
            else if (hit.collider.tag == "Barrel")
            {
                _params[0] = "Barrel";
                _params[1] = hit.point;
                hit.collider.gameObject.SendMessage("OnDamage", _params, SendMessageOptions.DontRequireReceiver);
                return;
            }
            // ****************************************************
            else
            {
                _params[0] = "Spark_Effect";
                _params[1] = hit.point;
                hit.collider.gameObject.transform.SendMessage("OnDamage", _params, SendMessageOptions.DontRequireReceiver);
                return;
            }
            
            _params[2] = hit.point;
            hit.collider.gameObject.transform.root.SendMessage("OnDamage", _params, SendMessageOptions.DontRequireReceiver);
        }
    }

    public void BloodEffect()
    {
        GameObject blood1;
        if(damagedPos.Equals(Vector3.zero))
        {
            blood1 = (GameObject)Instantiate(bloodEffect, transform.position, Quaternion.identity);
        }
        else
        {
            StopCoroutine(waitForDamaged);
            blood1 = (GameObject)Instantiate(bloodEffect, damagedPos, Quaternion.identity);
            damagedPos = Vector3.zero;
        }
        Destroy(blood1, 1.0f);

        Vector3 decalPos = transform.root.FindChild("BloodPos").position;
        Quaternion decalRot = Quaternion.Euler(90, 0, UnityEngine.Random.Range(0, 360));
        GameObject blood2 = (GameObject)Instantiate(bloodDecal, decalPos, decalRot);
        float scale = UnityEngine.Random.Range(1.5f, 3.5f);
        blood2.transform.localScale = Vector3.one * scale;
        Destroy(blood2, 5.0f);
    }

    public void OnDamage(object[] _params)
    {
        if(pv.isMine)
        {
            doVibrate();
            health -= (int)_params[1];
            if (health < 0)
            {
                health = 0;
            }
            hpBar.fillAmount = (float)health / (float)100;
            if (hpBar.fillAmount <= 0.3f)
            {
                hpBar.color = Color.red;
                hpText.color = Color.red;
                hpPoint.color = Color.red;
            }
            else if (hpBar.fillAmount <= 0.6f)
            {
                hpBar.color = Color.yellow;
                hpText.color = Color.yellow;
                hpPoint.color = Color.yellow;
            }
            hpPoint.text = health.ToString();

            if ((string)_params[0] == "Player_head")
            {
                pc.Status = 14;
            }
            else
            {
                if (health <= 0)
                {
                    pc.Status = 15;
                }
                else
                {
                    if (crouched)
                    {
                        pc.Status = 20;
                    }
                    else
                    {
                        if ((string)_params[0] == "Player_body")
                        {
                            pc.Status = 12;
                        }
                        else if ((string)_params[0] == "Player_branch")
                        {
                            pc.Status = 13;
                        }
                    }
                }
            }
        }
        else
        {
            waitForDamaged = WaitForDamaged((Vector3)_params[2]);
            StartCoroutine(waitForDamaged);
        }
    }

    IEnumerator WaitForDamaged(Vector3 point)
    {
        damagedPos = point;
        yield return new WaitForSeconds(1.5f);
        damagedPos = Vector3.zero;
    }

    public void aimEffect()
    {
        StartCoroutine(AimEffect());
    }

    IEnumerator AimEffect()
    {
        float delaytime = 200.0f;

        aimLeft.rectTransform.position = Vector3.Lerp(aimLeft.rectTransform.position, aimLeft.rectTransform.position + new Vector3(-15.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimRight.rectTransform.position = Vector3.Lerp(aimRight.rectTransform.position, aimRight.rectTransform.position + new Vector3(15.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimTop.rectTransform.position = Vector3.Lerp(aimTop.rectTransform.position, aimTop.rectTransform.position + new Vector3(0.0f, -15.0f, 0.0f), Time.deltaTime * delaytime);
        aimBottom.rectTransform.position = Vector3.Lerp(aimBottom.rectTransform.position, aimBottom.rectTransform.position + new Vector3(0.0f, 15.0f, 0.0f), Time.deltaTime * delaytime);
        yield return new WaitForSeconds(0.05f);
        aimLeft.rectTransform.position = Vector3.Lerp(aimLeft.rectTransform.position, aimLeft.rectTransform.position + new Vector3(5.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimRight.rectTransform.position = Vector3.Lerp(aimRight.rectTransform.position, aimRight.rectTransform.position + new Vector3(-5.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimTop.rectTransform.position = Vector3.Lerp(aimTop.rectTransform.position, aimTop.rectTransform.position + new Vector3(0.0f, 5.0f, 0.0f), Time.deltaTime * delaytime);
        aimBottom.rectTransform.position = Vector3.Lerp(aimBottom.rectTransform.position, aimBottom.rectTransform.position + new Vector3(0.0f, -5.0f, 0.0f), Time.deltaTime * delaytime);
        yield return new WaitForSeconds(0.05f);
        aimLeft.rectTransform.position = Vector3.Lerp(aimLeft.rectTransform.position, aimLeft.rectTransform.position + new Vector3(-5.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimRight.rectTransform.position = Vector3.Lerp(aimRight.rectTransform.position, aimRight.rectTransform.position + new Vector3(5.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimTop.rectTransform.position = Vector3.Lerp(aimTop.rectTransform.position, aimTop.rectTransform.position + new Vector3(0.0f, -5.0f, 0.0f), Time.deltaTime * delaytime);
        aimBottom.rectTransform.position = Vector3.Lerp(aimBottom.rectTransform.position, aimBottom.rectTransform.position + new Vector3(0.0f, 5.0f, 0.0f), Time.deltaTime * delaytime);
        yield return new WaitForSeconds(0.05f);
        aimLeft.rectTransform.position = Vector3.Lerp(aimLeft.rectTransform.position, aimLeft.rectTransform.position + new Vector3(5.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimRight.rectTransform.position = Vector3.Lerp(aimRight.rectTransform.position, aimRight.rectTransform.position + new Vector3(-5.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimTop.rectTransform.position = Vector3.Lerp(aimTop.rectTransform.position, aimTop.rectTransform.position + new Vector3(0.0f, 5.0f, 0.0f), Time.deltaTime * delaytime);
        aimBottom.rectTransform.position = Vector3.Lerp(aimBottom.rectTransform.position, aimBottom.rectTransform.position + new Vector3(0.0f, -5.0f, 0.0f), Time.deltaTime * delaytime);
        yield return new WaitForSeconds(0.05f);
        aimLeft.rectTransform.position = Vector3.Lerp(aimLeft.rectTransform.position, aimLeft.rectTransform.position + new Vector3(-5.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimRight.rectTransform.position = Vector3.Lerp(aimRight.rectTransform.position, aimRight.rectTransform.position + new Vector3(5.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimTop.rectTransform.position = Vector3.Lerp(aimTop.rectTransform.position, aimTop.rectTransform.position + new Vector3(0.0f, -5.0f, 0.0f), Time.deltaTime * delaytime);
        aimBottom.rectTransform.position = Vector3.Lerp(aimBottom.rectTransform.position, aimBottom.rectTransform.position + new Vector3(0.0f, 5.0f, 0.0f), Time.deltaTime * delaytime);
        yield return new WaitForSeconds(0.05f);
        aimLeft.rectTransform.position = Vector3.Lerp(aimLeft.rectTransform.position, aimLeft.rectTransform.position + new Vector3(15.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimRight.rectTransform.position = Vector3.Lerp(aimRight.rectTransform.position, aimRight.rectTransform.position + new Vector3(-15.0f, 0.0f, 0.0f), Time.deltaTime * delaytime);
        aimTop.rectTransform.position = Vector3.Lerp(aimTop.rectTransform.position, aimTop.rectTransform.position + new Vector3(0.0f, 15.0f, 0.0f), Time.deltaTime * delaytime);
        aimBottom.rectTransform.position = Vector3.Lerp(aimBottom.rectTransform.position, aimBottom.rectTransform.position + new Vector3(0.0f, -15.0f, 0.0f), Time.deltaTime * delaytime);
        yield return new WaitForSeconds(0.05f);
    }

    public bool fireBullet()
    {
        if(curBullets >= 3)
        {
            StartCoroutine(FireBullet());
            return true;
        }
        else
        {
            return false;
        }
    }

    IEnumerator FireBullet()
    {
        for(int i = 0; i < 3; i++)
        {
            curBullet.text = (--curBullets).ToString();
            yield return new WaitForSeconds(0.1f);
        }
    }

    /*
     * vibrate
     */
    public int doVibrate()
    {
        try
        {
            if(!pc.getGameover())
            {
                if (ajo.Call<int>("doVibrate") == 0)
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }
        }
        catch (Exception exc)
        {
            Debug.Log(exc.Message);
        }
        return -1;
    }

    /*
     * reload
     */
    public void doReload()
    {
        if(active && !f1g.getZoomed())
        {
            if(totalBullets < 30)
            {
                return;
            }
            active = false;
            if (crouched)
            {
                pc.Status = 19;
            }
            else
            {
                pc.Status = 11;
            }
            StartCoroutine(WaitForReload());
        }
    }

    IEnumerator WaitForReload()
    {
        yield return new WaitForSeconds(2.0f);
        active = true;
        curBullets = 30;
        totalBullets -= 30;
        curBullet.text = curBullets.ToString();
        totalBullet.text = " / " + totalBullets.ToString();
    }

    /*
     * crouch
     */
    public void swapCrouchStand()
    {
        if (crouched)
        {
            if (pv.isMine)
            {
                crouchImage.enabled = true;
                standImage.enabled = false;
                f1g.setCameraPos(false);
            }
            f1g.swapFocus(false);
            firePos = standFirePos;
            focus = standFocus;
            crouched = false;
        }
        else
        {
            if (pv.isMine)
            {
                crouchImage.enabled = false;
                standImage.enabled = true;
                f1g.setCameraPos(true);
            }
            f1g.swapFocus(true);
            firePos = crouchFirePos;
            focus = crouchFocus;
            crouched = true;
        }
    }

    /*
     * zoom
     */
    public void Zoom(string param)
    {
        if(active && pv.isMine)
        {
            f1g.setZoom(null);
        }
    }

    /*
     * game over
     */
    IEnumerator WaitForCrate()
    {
        int count = 0;
        while(true)
        {
            if(count >= 25)
            {
                getCrateBar.fillAmount = 1.0f;
                getCratePercent.text = "100%";
                yield return new WaitForSeconds(0.5f);
                getCratePanel.SetActive(false);
                break;
            }
            if (Vector3.Distance(transform.position, weaponsCrate.transform.position) < 5.0f && !pc.getGameover())
            {
                count++;
            }
            else
            {
                count = 0;
            }
            if(count > 0)
            {
                if(count == 1)
                {
                    getCratePanel.SetActive(true);
                }
                getCrateBar.fillAmount += 0.04f;
                getCratePercent.text = ((int)(getCrateBar.fillAmount * 100)) + "%";
            }
            else
            {
                getCrateBar.fillAmount = 0.0f;
                getCratePercent.text = "0%";
                getCratePanel.SetActive(false);
            }
            yield return new WaitForSeconds(0.1f);
        }
        pv.RPC("setGameover", PhotonTargets.Others, null);
    }

    [PunRPC]
    public void setGameover()
    {
        f1g.setGameover();
    }

    /*
     * update
     */
    void Update()
    {
        Debug.DrawRay(firePos.position, (focus.position - firePos.position) * 200.0f, Color.green);

        if(pv.isMine && !pc.getGameover())
        {
            if (gyro.userAcceleration.z > 1.0f)
            {
                doReload();
            }
        }
    }
}
