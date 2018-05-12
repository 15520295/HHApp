//package com.example.huydaoduc.hieu.chi.hhapp.Main.Driver.RouteRequestManager;
//
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.huydaoduc.hieu.chi.hhapp.Define;
//import com.example.huydaoduc.hieu.chi.hhapp.Model.Passenger.PassengerRequest;
//import com.example.huydaoduc.hieu.chi.hhapp.Model.RouteRequest.RouteRequest;
//import com.example.huydaoduc.hieu.chi.hhapp.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.scwang.smartrefresh.layout.SmartRefreshLayout;
//import com.scwang.smartrefresh.layout.api.RefreshLayout;
//import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
//import com.zaaach.toprightmenu.MenuItem;
//import com.zaaach.toprightmenu.TopRightMenu;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class WaitingPassengerRequestManagerFragment extends Fragment {
//
//    private OnWaitingPassengerRequestManagerListener mListener;
//    public interface OnWaitingPassengerRequestManagerListener {
//        void onFragmentInteraction(Uri uri);
//    }
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnWaitingPassengerRequestManagerListener) {
//            mListener = (OnWaitingPassengerRequestManagerListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnRouteRequestManagerFragmentListener");
//        }
//    }
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    public WaitingPassengerRequestManagerFragment() {
//    }
//
//    public static WaitingPassengerRequestManagerFragment newInstance(String param1, String param2) {
//        WaitingPassengerRequestManagerFragment fragment = new WaitingPassengerRequestManagerFragment();
//        Bundle args = new Bundle();
////        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
////            mParam1 = getArguments().getString(ARG_PARAM1);
//        }
//    }
//
//    private String getCurUid() {
//        return FirebaseAuth.getInstance().getCurrentUser().getUid();
//    }
//
//    RecyclerView rycv_request;
//    List<PassengerRequest> passengerRequests;
//
//    DatabaseReference dbRefe;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_waiting_passenger_request_manager, container, false);
//
//        Init(view);
//
//        Event(view);
//
//        refreshList(true);
//
//        return view;
//    }
//
//    private void Init(View view) {
//        //view
//
//        // recycler
//        initRecyclerView(view);
//
//        passengerRequests = new ArrayList<>();
//
//        // init database
//        dbRefe = FirebaseDatabase.getInstance().getReference();
//
//        initRefeshLayout(view);
//
//    }
//
//    private void Event(View view) {
//
//    }
//
//    //region -------------- Recycle View ----------------
//    private void initRecyclerView(View view) {
//        rycv_request = view.findViewById(R.id.recycler_view_requests);
//        LinearLayoutManager llm = new LinearLayoutManager(getContext());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        rycv_request.setLayoutManager(llm);
//    }
//
//    private void refreshList(boolean enableLoading) {
//        if (enableLoading)
//            startLoading();
//
//        FirebaseDatabase.getInstance().getReference()
//                .child(Define.DB_ROUTE_REQUESTS)
//                .child(getCurUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        passengerRequests.clear();
//                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                            PassengerRequest request = postSnapshot.getValue(PassengerRequest.class);
//
//                            passengerRequests.add(request);
//                        }
//
//                        Collections.reverse(passengerRequests);
//
//                        // Route request Row click event
//                        RouteRequestAdapter adapter = new RouteRequestAdapter(passengerRequests);
//                        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
//                            if (view.getId() == R.id.iv_menu) {
//                                showTopRightMenuItem(position, view);
//                            }
//                            if (view.getId() == R.id.btn_request_state) {
//
//
//                            }
//                        });
//
//                        rycv_request.setAdapter(adapter);
//                        stopLoading();
//                    }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void showTopRightMenuItem(int position, View view) {
//        TopRightMenu topRightMenu;
//        topRightMenu = new TopRightMenu(getActivity());
//
//        List<MenuItem> menuItems = new ArrayList<>();
//        menuItems.add( new MenuItem(R.drawable.ic_pause_20, getString(R.string.pause)));
//        menuItems.add( new MenuItem(R.drawable.ic_delete_20, getString(R.string.delete)));
//        menuItems.add( new MenuItem(R.drawable.ic_resume_20, getString(R.string.resume)));
//
//        topRightMenu
//                .setHeight(340)
//                .setWidth(320)
//                .showIcon(true)
//                .dimBackground(true)
//                .needAnimationStyle(true)
//                .setAnimationStyle(R.style.TRM_ANIM_STYLE)
//                .addMenuList(menuItems)
//                .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
//                    @Override
//                    public void onMenuItemClick(int menuPosition) {
//                        String command = null;
//
//                        if (menuPosition == 0) {
//                            command = "Pause";
//                        } else if (menuPosition == 1) {
//                            command = "Delete";
//                        } else if (menuPosition == 2) {
//                            command = "Resume";
//                        }
//
//                        changeRouteRequestState(position, command);
//                    }
//                })
//                .showAsDropDown(view, -250, 0);	//带偏移量
//    }
//
//    //endregion
//
//    //region -------------- Smart Refresh Layout ----------------
//
//    SmartRefreshLayout smartRefreshLayout;
//
//    private void initRefeshLayout(View view) {
//        smartRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
//        smartRefreshLayout.setEnableLoadMore(false);
//        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                refreshList(true);
//            }
//        });
//    }
//
//    private void startLoading() {
//        smartRefreshLayout.autoRefresh();
//
//    }
//
//    private void stopLoading() {
//        smartRefreshLayout.finishRefresh(500);
//    }
//
//    //endregion
//}
