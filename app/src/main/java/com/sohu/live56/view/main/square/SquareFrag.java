package com.sohu.live56.view.main.square;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.kurento.bean.RoomBean;
import com.sohu.kurento.netClient.KWWebSocketClient;
import com.sohu.kurento.util.LooperExecutor;
import com.sohu.live56.R;
import com.sohu.live56.bean.Room;
import com.sohu.live56.util.Constants;
import com.sohu.live56.util.LogCat;
import com.sohu.live56.view.main.player.ObserverActivity;
import com.sohu.live56.view.main.player.ObserverFrag;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

/**
 * 广场 fragment .
 * <p/>
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SquareFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SquareFrag extends Fragment implements AdapterView.OnItemClickListener {
    private TextView titletv;
    private ListView squarelv;
    private LinearLayout squarenodatefl;
    private SquareAdapter squareAdapter;
    private KWWebSocketClient socketClient = null;
    private boolean webSocketOk = false;
    private GifImageView progressImg = null;

    public static final String MASTER_KEY = "masterId";
    // TODO: Rename parameter arguments, choose names that match

    public static SquareFrag newInstance() {
        SquareFrag fragment = new SquareFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SquareFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        LogCat.debug("squareFragment create()----");
        socketClient = KWWebSocketClient.init();
        socketClient.setExecutor(new LooperExecutor());
        socketClient.setListListener(new ListListener());
        socketClient.connect(Constants.HOST_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_square, container, false);
        initialize(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socketClient.close();
    }

    private void initialize(View view) {

        titletv = (TextView) view.findViewById(R.id.title_tv);
        squarelv = (ListView) view.findViewById(R.id.square_lv);
        squarelv.setSelector(new BitmapDrawable());
        squarelv.setOnItemClickListener(this);
        squarenodatefl = (LinearLayout) view.findViewById(R.id.square_nodate_fl);
        progressImg = (GifImageView) view.findViewById(R.id.wifi_progress);

        squareAdapter = new SquareAdapter(getActivity().getApplicationContext());
        squarelv.setAdapter(squareAdapter);

        requestData();
    }

    /**
     * init data to listview.
     *
     * @param rooms
     */
    private void initData(ArrayList<Room> rooms) {
        if (rooms != null && rooms.size() > 0) {
            hasData();
            squareAdapter.notifyDataSetChanged(rooms);
        } else
            noData();

    }

    /**
     * get list data. Show progress icon if websocket is connecttiong.
     * Show no data if websocket connected failed.
     */
    private void requestData() {
        if (webSocketOk) {
            socketClient.sendListerRequest();

        } else {
            noData();
        }

    }

    private void noData() {
        squarelv.setVisibility(View.GONE);
        squarenodatefl.setVisibility(View.VISIBLE);
    }

    private void progressWifi() {
        squarenodatefl.setVisibility(View.VISIBLE);
    }

    private void hasData() {
        squarelv.setVisibility(View.VISIBLE);
        squarenodatefl.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getActivity(), ObserverActivity.class);
        SquareAdapter adapter = (SquareAdapter) parent.getAdapter();
        Room room = (Room) adapter.getItem(position);
        intent.putExtra(MASTER_KEY, String.valueOf(room.getName()));
        LogCat.debug("start room Id : " + room.getSessionId());
        getActivity().startActivity(intent);
    }

    private class ListListener implements KWWebSocketClient.OnListListener {

        @Override
        public void onListListener(final ArrayList<RoomBean> rooms) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Room> viewRooms = new ArrayList<Room>();
                    for (RoomBean roomBean : rooms) {
                        Room room = new Room();
                        room.setSessionId(roomBean.getSessionId());
                        room.setName(roomBean.getName());
                        room.setState(Room.State.LIVING);
                        viewRooms.add(room);
                    }

                    initData(viewRooms);
                }
            });
        }

        @Override
        public void onConnectionOpened() {
            webSocketOk = true;
            requestData();
        }

        @Override
        public void onError(final String msg) {
//            webSocketOk = false;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initData(null);
//                    Toast.makeText(getActivity().getApplicationContext(), "websocket connect failed : " + msg, Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onClose(String msg) {
//            webSocketOk = false;
            LogCat.debug("websocket colsed...." + msg);
        }
    }
}
