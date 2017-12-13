package gos.media.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import gos.media.R;
import gos.media.adapter.RemoterSetting;
import gos.media.data.IndexClass;
import gos.media.define.CommandType;
import gos.media.define.DataParse;
import gos.media.define.KeyValue;
import gos.media.event.EventManager;
import gos.media.event.EventMode;
import gos.media.event.EventMsg;
import gos.media.view.TitleBar;

import static gos.media.define.KeyValue.KEYVALUE_DOWN;
import static gos.media.define.KeyValue.KEYVALUE_LEFT;
import static gos.media.define.KeyValue.KEYVALUE_OK;
import static gos.media.define.KeyValue.KEYVALUE_RIGHT;
import static gos.media.define.KeyValue.KEYVALUE_UP;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RemoteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RemoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RemoteFragment extends Fragment implements View.OnLongClickListener,View.OnTouchListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private  final String TAG = this.getClass().getSimpleName();
    private View rootView = null;   //缓存页面
    private Context context;
    private Button btnUp,btnDown,btnLeft,btnRight;
    private TextView btnVolUp,btnVolDown,btnCHUp,btnCHDown;
    private LinearLayout layoutEpg;
    private LinearLayout layoutProglist;
    private boolean isLongKey = false;  //是否长按
    HashMap<Integer,Integer> keysMap = new HashMap();

    private ImageButton settingsRemote ;
    private enum KeyStatus{
        NORMAL, //正常调台
        LONG,  // 长按
        UP    //取消长按
    }

    private final int WHAT_SETTING_REMOTE_CANCLE = 1;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_SETTING_REMOTE_CANCLE:
                    settingsRemote.setSelected(false);
                    break;
                default:
                    break;
            }
        }
    };

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RemoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RemoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RemoteFragment newInstance(String param1, String param2) {
        RemoteFragment fragment = new RemoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        EventManager.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(null == rootView){
            //Log.i(TAG,"null == rootView");
            rootView   = inflater.inflate(R.layout.fragment_remote, container, false);
            initLayout(rootView);
        }

        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if(null != parent){
            parent.removeView(rootView);
        }

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onLongClick(View view) {
        if (sendRemoteKey(view.getId(), KeyStatus.LONG)) {
            isLongKey = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_UP){
            if(isLongKey) {
                isLongKey = false;
                if (sendRemoteKey(view.getId(), KeyStatus.UP)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    /**
     * 接收内部事件
     * @param msg   接收的消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecviveEvent(EventMsg msg){
        if(EventMode.IN == msg.getEventMode()){  //对内
            switch (msg.getCommand()){
                case CommandType.COM_SYS_REMOTE_ID:
                    IndexClass IndexClass = DataParse.getIndexClass(msg.getData());
                    sendRemoteKey(IndexClass.getIndex(),KeyStatus.NORMAL);
                    break;
                default:
                    break;
            }
        }
    }


    protected void initLayout(View view) {

        TitleBar mTitleBar = (TitleBar) view.findViewById(R.id.titlebar);
        mTitleBar.setTitleInfoWithText(R.string.app_name);

        TextView figureBn = (TextView)rootView.findViewById(R.id.figure);
        figureBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFuncFrame();
            }
        });

        TextView funcBn = (TextView)rootView.findViewById(R.id.funcButton);
        funcBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFigureFrame();
            }
        });

        settingsRemote = (ImageButton)rootView.findViewById(R.id.settings_remote);
        initView();
        initKeysMap();
    }

    private void initView(){
        /*btnUp      = (Button) rootView.findViewById(R.id.up);
        btnDown    = (Button) rootView.findViewById(R.id.down);
        btnLeft    = (Button) rootView.findViewById(R.id.left);
        btnRight   = (Button) rootView.findViewById(R.id.right);*/

        btnVolUp   = (TextView) rootView.findViewById(R.id.vol_add);
        btnVolDown = (TextView) rootView.findViewById(R.id.vol_sub);
        btnCHUp    = (TextView) rootView.findViewById(R.id.ch_add);
        btnCHDown  = (TextView) rootView.findViewById(R.id.ch_sub);

        /*btnUp.setOnLongClickListener(this);
        btnDown.setOnLongClickListener(this);
        btnLeft.setOnLongClickListener(this);
        btnRight.setOnLongClickListener(this);*/

        btnVolUp.setOnLongClickListener(this);
        btnVolDown.setOnLongClickListener(this);
        btnCHUp.setOnLongClickListener(this);
        btnCHDown.setOnLongClickListener(this);

        /*btnUp.setOnTouchListener(this);
        btnDown.setOnTouchListener(this);
        btnLeft.setOnTouchListener(this);
        btnRight.setOnTouchListener(this);*/

        btnVolUp.setOnTouchListener(this);
        btnVolDown.setOnTouchListener(this);
        btnCHUp.setOnTouchListener(this);
        btnCHDown.setOnTouchListener(this);

        RemoterSetting remoteSet = (RemoterSetting) rootView.findViewById(R.id.remoteSet);
        remoteSet.setKeyValue(KEYVALUE_UP, KEYVALUE_DOWN, KEYVALUE_LEFT, KEYVALUE_RIGHT, KEYVALUE_OK);
        remoteSet.setOnTouchListener(new RemoterSetting.onTouchListener() {

            @Override
            public void click(int keyValue) {
                sendKeyValue(keyValue, KeyStatus.NORMAL);
            }

            @Override
            public void longClick(final int keyValue) {
                getActivity().runOnUiThread(new Runnable() {//更新到ui线程
                    @Override
                    public void run() {
                        sendKeyValue(keyValue, KeyStatus.LONG);
                    }
                });
            }

            @Override
            public void cancelLong(int keyValue) {
                sendKeyValue(keyValue, KeyStatus.UP);
            }
        });

    }

    public void initKeysMap()
    {
        keysMap.put(R.id.power,KeyValue.KEYVALUE_POWER);
        keysMap.put(R.id.func1,KeyValue.KEYVALUE_FUNC1);
        keysMap.put(R.id.exit,KeyValue.KEYVALUE_EXIT);
        keysMap.put(R.id.menue,KeyValue.KEYVALUE_MENUE);
        keysMap.put(R.id.info,KeyValue.KEYVALUE_INFO);
        keysMap.put(R.id.back,KeyValue.KEYVALUE_BACK);

        /* keysMap.put(R.id.up,KeyValue.KEYVALUE_UP);
       keysMap.put(R.id.left,KeyValue.KEYVALUE_LEFT);
        keysMap.put(R.id.ok,KeyValue.KEYVALUE_OK);
        keysMap.put(R.id.right,KeyValue.KEYVALUE_RIGHT);
        keysMap.put(R.id.down,KeyValue.KEYVALUE_DOWN);*/
        keysMap.put(R.id.vol_add,KeyValue.KEYVALUE_VOL_ADD);
        keysMap.put(R.id.ch_add,KeyValue.KEYVALUE_CH_ADD);
        keysMap.put(R.id.vol_sub,KeyValue.KEYVALUE_VOL_SUB);
        keysMap.put(R.id.fav,KeyValue.KEYVALUE_FAV);


        keysMap.put(R.id.ch_sub,KeyValue.KEYVALUE_CH_SUB);
        keysMap.put(R.id.guide,KeyValue.KEYVALUE_GUIDE);

        keysMap.put(R.id.mute,KeyValue.KEYVALUE_MUTE);

        keysMap.put(R.id.pvr,KeyValue.KEYVALUE_PVR);

        //number
        keysMap.put(R.id.number0,KeyValue.KEYVALUE_0);
        keysMap.put(R.id.number1,KeyValue.KEYVALUE_1);
        keysMap.put(R.id.number2,KeyValue.KEYVALUE_2);
        keysMap.put(R.id.number3,KeyValue.KEYVALUE_3);
        keysMap.put(R.id.number4,KeyValue.KEYVALUE_4);
        keysMap.put(R.id.number5,KeyValue.KEYVALUE_5);
        keysMap.put(R.id.number6,KeyValue.KEYVALUE_6);
        keysMap.put(R.id.number7,KeyValue.KEYVALUE_7);
        keysMap.put(R.id.number8,KeyValue.KEYVALUE_8);
        keysMap.put(R.id.number9,KeyValue.KEYVALUE_9);


        keysMap.put(R.id.btn_red,KeyValue.KEYVALUE_RED);
        keysMap.put(R.id.btn_green,KeyValue.KEYVALUE_GREEN);
        keysMap.put(R.id.btn_yellow,KeyValue.KEYVALUE_YELLOW);
        keysMap.put(R.id.btn_blue,KeyValue.KEYVALUE_BLUE);

        keysMap.put(R.id.fast_rewind,KeyValue.KEYVALUE_REW);
        keysMap.put(R.id.play,KeyValue.KEYVALUE_PLAY);
        keysMap.put(R.id.pause,KeyValue.KEYVALUE_PAUSE);
        keysMap.put(R.id.fast_forward,KeyValue.KEYVALUE_FF);
        keysMap.put(R.id.skip_previous,KeyValue.KEYVALUE_PRE);
        keysMap.put(R.id.record,KeyValue.KEYVALUE_REC);
        keysMap.put(R.id.stop,KeyValue.KEYVALUE_STOP);
        keysMap.put(R.id.skip_next,KeyValue.KEYVALUE_NEXT);

        keysMap.put(R.id.set,KeyValue.KEYVALUE_SET);
        keysMap.put(R.id.excite,KeyValue.KEYVALUE_EXCITE);
        keysMap.put(R.id.help,KeyValue.KEYVALUE_HELP);



    }
    public void hideFuncFrame()
    {
        ViewGroup function = (ViewGroup) rootView.findViewById(R.id.function);
        ViewGroup digitFrame = (ViewGroup) rootView.findViewById(R.id.digit);
        function.setVisibility(View.GONE);
        digitFrame.setVisibility(View.VISIBLE);
    }

    public void hideFigureFrame()
    {
        ViewGroup function = (ViewGroup) rootView.findViewById(R.id.function);
        ViewGroup digitFrame = (ViewGroup) rootView.findViewById(R.id.digit);
        function.setVisibility(View.VISIBLE);
        digitFrame.setVisibility(View.GONE);
    }


    public boolean sendRemoteKey(int id, KeyStatus keyStatus) {
        int keyValue = -1;
        Integer key = new Integer(id);
        if(keysMap.containsKey(key))
        {
            keyValue = keysMap.get(key);
        }
        if (-1 != keyValue) {
            sendKeyValue(keyValue, keyStatus);
        }
        return false;
    }

    public boolean sendKeyValue(int keyValue, KeyStatus keyStatus) {
        Log.i("fragment_remote","keyValue:" + keyValue);
        IndexClass indexClass = new IndexClass(keyValue);
        if(keyStatus == KeyStatus.LONG) {
            EventManager.send(CommandType.COM_REMOTE_SET_LONG_KEY, JSON.toJSONString(indexClass), EventMode.OUT);
        }else if(keyStatus == KeyStatus.UP){
            EventManager.send(CommandType.COM_REMOTE_SET_KEY_UP, JSON.toJSONString(indexClass), EventMode.OUT);
        }else{
            EventManager.send(CommandType.COM_REMOTE_SET_KEY, JSON.toJSONString(indexClass), EventMode.OUT);
        }
        settingsRemote.setSelected(true);
        handler.sendEmptyMessageDelayed(WHAT_SETTING_REMOTE_CANCLE,300);
        return true;
    }

}
