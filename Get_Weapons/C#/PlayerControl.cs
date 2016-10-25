using System.Collections;
using UnityEngine;
using System;

public class PlayerControl : MonoBehaviour {
    private PhotonView pv = null;
    private Rigidbody rb;
    private Transform tr;
    private UIControl uc;
    private WeaponControl wc;

    private AudioSource _as = null;
    private AudioClip fireSfx;
    public AudioClip Assault74MFireSound;
    public AudioClip Assault91FireSound;
    public AudioClip Assault971FireSound;
    public AudioClip Assault2002FireSound;
    public AudioClip AssaultMASFireSound;
    private AudioClip reloadSfx;
    public AudioClip Assault74MReloadSound;
    public AudioClip Assault91ReloadSound;
    public AudioClip Assault971ReloadSound;
    public AudioClip Assault2002ReloadSound;
    public AudioClip AssaultMASReloadSound;
    public AudioClip stepSound;

    private IEnumerator stepSoundRoutine;

    private Vector3 moveDir;
    private float moveSpeed = 5.0f;

    private int bodyDamage = 10;
    private int branchDamage = 5;

    private Animator am;

    private bool gameover;

    private int status;
    private bool actable;

    private Vector3 curPos = Vector3.zero;
    private Quaternion curRot = Quaternion.identity;

    private IEnumerator coroutine;
    private IEnumerator continueStatus;
    private IEnumerator crouchedStatus;

    // for transparent
    public GameObject body;
    public GameObject equip;
    public GameObject weapon;
    public Texture standard;
    public Texture dark;
    public Texture snow;

    private Transform standFocus;
    private Transform crouchFocus;

    void Awake () {
        pv = GetComponent<PhotonView>();
        rb = GetComponent<Rigidbody>();
        tr = GetComponent<Transform>();
        uc = GetComponent<UIControl>();
        am = GetComponent<Animator>();
        _as = GetComponent<AudioSource>();

        pv.synchronization = ViewSynchronization.UnreliableOnChange;
        pv.ObservedComponents[0] = this;

        standFocus = transform.FindChild("StandFocus");
        crouchFocus = transform.FindChild("CrouchFocus");

        string[] param = (string[])pv.instantiationData;

        if (pv.isMine)
        {
            rb.centerOfMass = new Vector3(0.0f, -0.5f, 0.0f);

            // make a transparent body in oneself view
            string shaderName = "Legacy Shaders/Transparent/Diffuse";
            body.GetComponent<SkinnedMeshRenderer>().material.shader = Shader.Find(shaderName);
            body.GetComponent<SkinnedMeshRenderer>().material.color = new Color(0, 0, 0, 0);
            equip.SetActive(false);
            weapon.GetComponent<MeshRenderer>().enabled = false;

            status = 0;
        }
        else
        {
            rb.isKinematic = true;
            status = -1;

            // set costume
            if (string.Equals(param[0], "Standard"))
            {
                body.GetComponent<SkinnedMeshRenderer>().material.mainTexture = standard;
                equip.GetComponent<SkinnedMeshRenderer>().material.mainTexture = standard;
            }
            else if (string.Equals(param[0], "Dark"))
            {
                body.GetComponent<SkinnedMeshRenderer>().material.mainTexture = dark;
                equip.GetComponent<SkinnedMeshRenderer>().material.mainTexture = dark;
            }
            else if(string.Equals(param[0], "Snow"))
            {
                body.GetComponent<SkinnedMeshRenderer>().material.mainTexture = snow;
                equip.GetComponent<SkinnedMeshRenderer>().material.mainTexture = snow;
            }
        }

        // set damages according to the weapon
        if (string.Equals(param[1], "Assault74M"))
        {
            bodyDamage = 10;
            branchDamage = 5;
            fireSfx = Assault74MFireSound;
            reloadSfx = Assault74MReloadSound;
        }
        else if (string.Equals(param[1], "Assault-91"))
        {
            bodyDamage = 12;
            branchDamage = 6;
            fireSfx = Assault91FireSound;
            reloadSfx = Assault91ReloadSound;
        }
        else if (string.Equals(param[1], "Assault971"))
        {
            bodyDamage = 15;
            branchDamage = 7;
            fireSfx = Assault971FireSound;
            reloadSfx = Assault971ReloadSound;
        }
        else if (string.Equals(param[1], "Assault2002"))
        {
            bodyDamage = 17;
            branchDamage = 8;
            fireSfx = Assault2002FireSound;
            reloadSfx = Assault2002ReloadSound;
        }
        else if (string.Equals(param[1], "AssaultMAS"))
        {
            bodyDamage = 20;
            branchDamage = 10;
            fireSfx = AssaultMASFireSound;
            reloadSfx = AssaultMASReloadSound;
        }

        gameover = false;

        curPos = tr.position;
        curRot = tr.rotation;
        actable = true;
    }

    public void setWeapon(string weaponName)
    {
        wc = GameObject.Find(weaponName + "(Clone)").GetComponent<WeaponControl>();
    }

    public void setDefaultSetting()
    {
        uc.setDefaultSetting(bodyDamage, branchDamage);
    }

    // sync
    void OnPhotonSerializeView(PhotonStream stream, PhotonMessageInfo info)
    {
        if (stream.isWriting)
        {
            stream.SendNext(tr.position);
            stream.SendNext(tr.rotation);
            stream.SendNext(status);
            stream.SendNext(standFocus.position);
            stream.SendNext(crouchFocus.position);
        }
        else
        {
            curPos = (Vector3)stream.ReceiveNext();
            curRot = (Quaternion)stream.ReceiveNext();
            Status = (int)stream.ReceiveNext();
            standFocus.position = (Vector3)stream.ReceiveNext();
            crouchFocus.position = (Vector3)stream.ReceiveNext();
        }
    }

    // some constraint for animation and action
    public int Status
    {
        get
        {
            return status;
        }
        set
        {
            if (gameover)
            {
                return;
            }

            //if(value > 0 && value < 9)
            //{
            //    stepSoundRoutine = StepSound();
            //    StartCoroutine(stepSoundRoutine);
            //}
            //else
            //{
            //    StopCoroutine(stepSoundRoutine);
            //}

            // animations
            switch (value)
            {
                // basic movement
                case 0:
                    if (pv.isMine)
                    {
                        if(actable)
                        {
                            am.SetInteger("setAnimation", value);
                            wc.Status = 0;
                        }
                        else
                        {
                            return;
                        }
                    }
                    else
                    {
                        try
                        {
                            StopAllCoroutines();
                            StopCoroutine(crouchedStatus);
                        }
                        catch (Exception exc) { string nul = exc.Message; }
                        am.SetInteger("setAnimation", value);
                        return;
                    }
                    break;

                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    if(pv.isMine)
                    {
                        if(actable)
                        {
                            am.SetInteger("setAnimation", value);
                            wc.Status = 1;
                        }
                        else
                        {
                            return;
                        }
                    }
                    else
                    {
                        try
                        {
                            StopAllCoroutines();
                            StopCoroutine(crouchedStatus);
                        }
                        catch (Exception exc) { string nul = exc.Message; }
                        am.SetInteger("setAnimation", value);
                        return;
                    }
                    break;

                // fire
                case 10:
                case 18:
                    if(pv.isMine)
                    {
                        if(!uc.fireBullet())
                        {
                            return;
                        }
                    }
                    coroutine = Bursting(value, 0.3f);
                    StartCoroutine(coroutine);
                    if (!pv.isMine)
                    {
                        return;
                    }
                    break;

                // damaged
                case 12:
                case 13:
                case 20:
                    if(!pv.isMine)
                    {
                        uc.BloodEffect();
                    }
                    if(actable)
                    {
                        StartCoroutine(WaitForAnimation(value, 1.0f));
                    }
                    if (!pv.isMine)
                    {
                        return;
                    }
                    break;

                // death
                case 14:
                case 15:
                    if (!pv.isMine)
                    {
                        uc.BloodEffect();
                    }
                    am.SetInteger("setAnimation", value);
                    gameover = true;
                    StopAllCoroutines();
                    if (pv.isMine)
                    {
                        wc.Status = value;
                    }
                    else
                    {
                        return;
                    }
                    break;

                // crouch
                case 16:
                    if (!uc.getCrouch())
                    {
                        uc.swapCrouchStand();
                    }
                    try
                    {
                        StopAllCoroutines();
                    }
                    catch (Exception exc) { string nul = exc.Message; }
                    am.SetInteger("setAnimation", value);
                    if (pv.isMine)
                    {
                        actable = false;
                        crouchedStatus = CrouchedStatus(value);
                        StartCoroutine(crouchedStatus);
                    }
                    else
                    {
                        return;
                    }
                    break;

                // reload
                case 11:
                case 19:
                    _as.PlayOneShot(reloadSfx, 1.0f);
                    StartCoroutine(WaitForAnimation(value, 2.0f));
                    if(!pv.isMine)
                    {
                        return;
                    }
                    break;

                // stand
                case 21:
                    if (uc.getCrouch())
                    {
                        uc.swapCrouchStand();
                    }
                    if (pv.isMine)
                    {
                        StopCoroutine(crouchedStatus);
                        StartCoroutine(WaitForAnimation(0, 0.35f));
                    }
                    else
                    {
                        am.SetInteger("setAnimation", 0);
                        return;
                    }
                    break;

                // stop bursting
                case 22:
                    if(coroutine != null)
                    {
                        try
                        {
                            StopCoroutine(coroutine);
                        }
                        catch (Exception exc) { string nul = exc.Message; }
                    }
                    wc.Status = 0;
                    uc.setActive(true);
                    if (uc.getCrouch())
                    {
                        am.SetInteger("setAnimation", 16);
                        status = 16;
                        if(pv.isMine)
                        {
                            crouchedStatus = CrouchedStatus(16);
                            StartCoroutine(crouchedStatus);
                        }
                    }
                    else
                    {
                        am.SetInteger("setAnimation", 0);
                        actable = true;
                    }
                    return;
                
                // zoom
                case 23:
                    if(actable && pv.isMine)
                    {
                        if (moveSpeed == 5.0f)
                        {
                            moveSpeed = 2.5f;
                        }
                        else
                        {
                            moveSpeed = 5.0f;
                        }
                    }
                    return;
            }
            status = value;
        }
    }

    IEnumerator Bursting(int value, float delay)
    {
        if(pv.isMine)
        {
            actable = false;
            wc.Status = value;
            if(uc.getCrouch())
            {
                StopCoroutine(crouchedStatus);
            }
        }
        am.SetInteger("setAnimation", value);
        while (true)
        {
            uc.raycastHit(3);
            StartCoroutine(FiringSound());
            if (pv.isMine)
            {
                uc.aimEffect();
                status = value;
            }
            yield return new WaitForSeconds(delay);
            if(pv.isMine)
            {
                if(!uc.fireBullet())
                {
                    Status = 22;
                    break;
                }
            }
        }
    }

    IEnumerator WaitForAnimation(int value, float delay)
    {
        if (pv.isMine)
        {
            actable = false;
            wc.Status = value;
            if(uc.getCrouch())
            {
                StopCoroutine(crouchedStatus);
            }
            continueStatus = ContinueStatus(value);
            StartCoroutine(continueStatus);
        }
        am.SetInteger("setAnimation", value);
        yield return new WaitForSeconds(delay);
        if(!uc.getCrouch())
        {
            if(pv.isMine)
            {
                StopCoroutine(continueStatus);
                actable = true;
            }
        }
        else
        {
            if(pv.isMine)
            {
                StopCoroutine(continueStatus);
                crouchedStatus = CrouchedStatus(16);
                StartCoroutine(crouchedStatus);
                status = 16;
                wc.Status = 0;
            }
        }
    }

    IEnumerator ContinueStatus(int value)
    {
        while(true)
        {
            yield return new WaitForSeconds(0.5f);
            status = value;
        }
    }

    IEnumerator CrouchedStatus(int value)
    {
        while(true)
        {
            yield return new WaitForSeconds(0.5f);
            status = value;
        }
    }
    
    IEnumerator FiringSound()
    {
        for(int i = 0; i < 3; i++)
        {
            _as.PlayOneShot(fireSfx, 1.0f);
            yield return new WaitForSeconds(0.05f);
        }
    }

    IEnumerator StepSound()
    {
        while(true)
        {
            _as.PlayOneShot(stepSound, 1.0f);
            yield return new WaitForSeconds(1.0f);
        }
        
    }

    public bool getGameover()
    {
        return gameover;
    }

    public void setGameover(bool value)
    {
        gameover = value;
    }

    public void setActable(bool value)
    {
        actable = value;
    }

    void Update()
    {
        if (pv.isMine)
        {
            float accelX = Input.acceleration.x;
            float accelY = -Input.acceleration.y;

            // character movement for left and right
            if (accelX < -0.8f)
            {
                accelX = 0.0f;
            }
            else if (accelX < -0.15f)
            {

            }
            else if (accelX < 0.15f)
            {
                accelX = 0.0f;
            }
            else if (accelX < 0.8f)
            {

            }
            else
            {
                accelX = 0.0f;
            }

            // character movement for forward and backward
            if (accelY < 0.0f)
            {
                accelY = 0.0f;
            }
            else if (accelY < 0.65f)
            {

            }
            else if (accelY < 0.85f)
            {
                accelY = 0.0f;
            }
            else if (accelY < 1.0f)
            {
                accelY = -accelY + 0.5f;
            }
            else
            {
                accelY = 0.0f;
            }

            moveDir = (new Vector3(accelX, 0, accelY)).normalized;
            if(actable && !gameover)
            {
                tr.Translate(moveDir * Time.deltaTime * moveSpeed, Space.Self);
            }

            // set player status
            if (moveDir.x == 0 && moveDir.z == 0)
            {
                Status = 0;
            }
            else
            {
                if (moveDir.x == 1)
                {
                    Status = 3;
                }
                else if (moveDir.x == -1)
                {
                    Status = 4;
                }
                else if (moveDir.x == 0)
                {
                    if (moveDir.z == 1)
                    {
                        Status = 1;
                    }
                    else
                    {
                       Status = 2;
                    }
                }
                else
                {
                    if (moveDir.x < 0)
                    {
                        if (moveDir.z > 0)
                        {
                            Status = 6;
                        }
                        else
                        {
                            Status = 8;
                        }
                    }
                    else
                    {
                        if (moveDir.z > 0)
                        {
                            Status = 5;
                        }
                        else
                        {
                            Status = 7;
                        }
                    }
                }
            }
        }
        else
        {
            tr.position = Vector3.Lerp(tr.position, curPos, Time.deltaTime * 3.0f);
            tr.rotation = Quaternion.Slerp(tr.rotation, curRot, Time.deltaTime * 3.0f);
        }
    }
}
