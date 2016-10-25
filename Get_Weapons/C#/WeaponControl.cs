using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System;

public class WeaponControl : MonoBehaviour {
    private Animator am;
    private int status;
    public MeshRenderer muzzleFlash;
    private IEnumerator muzzleCoroutine;
    private IEnumerator firingCoroutine;

	void Start () {
        am = GetComponent<Animator>();
        status = 0;
        muzzleFlash.enabled = false;
    }

    public int Status
    {
        get
        {
            return status;
        }
        set
        {
            if(value == 10 || value == 18)
            {
                muzzleCoroutine = ShowMuzzleFlash();
                StartCoroutine(muzzleCoroutine);
            }
            else if(value == 0 && (status == 10 || status == 18))
            {
                try
                {
                    StopAllCoroutines();
                }
                catch (Exception exc) { string nul = exc.Message; }
                muzzleFlash.enabled = false;
            }
            am.SetInteger("param", value);
            status = value;
        }
    }

    IEnumerator ShowMuzzleFlash()
    {
        while(true)
        {
            for(int i = 0; i < 3; i++)
            {
                float scale = UnityEngine.Random.Range(0.3f, 0.5f);
                muzzleFlash.transform.localScale = Vector3.one * scale;
                Quaternion rot = Quaternion.Euler(0, 0, UnityEngine.Random.Range(0, 360));
                muzzleFlash.transform.localRotation = rot;
                muzzleFlash.enabled = true;
                yield return new WaitForSeconds(0.05f);
                muzzleFlash.enabled = false;
                yield return new WaitForSeconds(0.1f);
            }
            // yield return new WaitForSeconds(1.5f);
        }
    }

    IEnumerator Bursting(int value)
    {
        while(true)
        {
            am.SetInteger("param", value);
            yield return new WaitForSeconds(0.4f);
            am.SetInteger("param", 0);
            yield return new WaitForSeconds(0.2f);
        }
    }
}
