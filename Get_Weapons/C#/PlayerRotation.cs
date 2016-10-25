using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;

public class PlayerRotation : MonoBehaviour {
    private PhotonView pv = null;
    private UIControl uc = null;

    private Transform tr;

    private GameObject scrollBoard;

    private float beforePositionX;
    private float beforePositionY;
    private float rotSpeed = 5.0f;

    private bool actable;



    void Awake () {
        pv = GetComponent<PhotonView>();
        tr = GetComponent<Transform>();
        uc = GetComponent<UIControl>();

        actable = true;

        if(pv.isMine)
        {
            scrollBoard = GameObject.Find("ScrollBoard") as GameObject;
            EventTrigger trigger = scrollBoard.GetComponent<EventTrigger>();

            EventTrigger.Entry onBeginDrag = new EventTrigger.Entry();
            onBeginDrag.eventID = EventTriggerType.BeginDrag;
            onBeginDrag.callback.AddListener((data) => { OnBeginDragDelegate((PointerEventData)data); });
            trigger.triggers.Add(onBeginDrag);

            EventTrigger.Entry onDrag = new EventTrigger.Entry();
            onDrag.eventID = EventTriggerType.Drag;
            onDrag.callback.AddListener((data) => { OnDragDelegate((PointerEventData)data); });
            trigger.triggers.Add(onDrag);
        }
    }

    public bool getActable()
    {
        return actable;
    }

    public void setActable(bool value)
    {
        actable = value;
    }

    public void OnBeginDragDelegate(PointerEventData eventData)
    {
        beforePositionX = eventData.pressPosition.x;
        beforePositionY = eventData.pressPosition.y;
    }

    public void OnDragDelegate(PointerEventData eventData)
    {
        if(actable && pv.isMine)
        {
            tr.Rotate(0, ((eventData.position.x - beforePositionX)/2) * Time.deltaTime * rotSpeed, 0);
            float value = (eventData.position.y - beforePositionY) / 10.0f;
            uc.setFocus(value);
            beforePositionX = eventData.position.x;
            beforePositionY = eventData.position.y;
        }
    }
}