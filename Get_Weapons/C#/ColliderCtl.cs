using UnityEngine;
using System.Collections;

public class ColliderCtl : MonoBehaviour {
    public GameObject sparkEffect;

    public void OnDamage(object[] _params)
    {
        if ((string)_params[0] == "Spark_Effect")
        {
            GameObject spark = (GameObject)Instantiate(sparkEffect, (Vector3)_params[1], Quaternion.identity);
            Destroy(spark, spark.GetComponent<ParticleSystem>().duration + 0.2f);
        }}
}
