package com.pommedeterresautee.twoborange3

import android.app.Activity
import android.support.v4.widget.DrawerLayout

import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.view.GravityCompat
import android.content.Intent
import android.view.{Gravity, ViewGroup, View}
import android.os.Bundle
import android.widget.{TextView, BaseAdapter, AdapterView, ListView}
import android.widget.AdapterView.OnItemClickListener
import android.util.TypedValue

/**
 * Trait to add a side menu to an Activity
 */
trait SideMenu extends Activity {

  case class SideMenuItem (mTitle: Int, mIcon: Int, mIntent: Intent, mShouldFinishCurrentActivity: Boolean = true)

  private var mDrawerLayout: DrawerLayout = _
  private var mDrawerList: ListView = _
  private var mDrawerToggle: ActionBarDrawerToggle = _
  private var mMenuList: List[SideMenuItem] = _


  override def onCreate(savedInstanceState: Bundle){
    super.onCreate(savedInstanceState)
    //Init side menu content
    mMenuList = List[SideMenuItem](SideMenuItem(R.string.app_name, R.drawable.ic_drawer, null))
    getActionBar.setDisplayHomeAsUpEnabled(true)
    getActionBar.setHomeButtonEnabled(true)
  }

  override def onStart(){
    super.onStart()
    if (mDrawerLayout == null) {
      mDrawerLayout = findViewById(R.id.drawer_layout).asInstanceOf[DrawerLayout]
      mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
      mDrawerList = findViewById(R.id.sidemenu_drawer).asInstanceOf[ListView]
      mDrawerList.setAdapter(new SideMenuAdapter())
      mDrawerList.setOnItemClickListener(new DrawerItemClickListener())
      mDrawerToggle = new CustomActionBarDrawerToggle(this, mDrawerLayout,
        R.drawable.ic_drawer, R.string.open, R.string.close)
      mDrawerLayout.setDrawerListener(mDrawerToggle)
    }
    mDrawerToggle.syncState()
  }


class CustomActionBarDrawerToggle (var activity: Activity, var drawerLayout: DrawerLayout, var drawerImageRes: Int, var openDrawerContentDescRes: Int, var closeDrawerContentDescRes: Int) extends ActionBarDrawerToggle(activity, drawerLayout, drawerImageRes,  openDrawerContentDescRes, closeDrawerContentDescRes) {

  override def onDrawerClosed(view: View) = invalidateOptionsMenu()
  override def onDrawerOpened(drawerView: View) = invalidateOptionsMenu()
}

  class DrawerItemClickListener extends OnItemClickListener {
    def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long):Unit ={
    val item: SideMenuItem = mMenuList(position)
    startActivity(item.mIntent)
    if (item.mShouldFinishCurrentActivity) {
      finish()
      overridePendingTransition(0, 0)
    } else {
      mDrawerLayout.closeDrawers()
    }
}
  }

class SideMenuAdapter extends BaseAdapter {

  override def getCount:Int = mMenuList.size

  override def getItem(position: Int) : Object = mMenuList(position)


  override def getItemId(position: Int) = position

  override def getView(position: Int, convertView: View , parent: ViewGroup): View = {

    val item : SideMenuItem = mMenuList(position)
    val paddingLeft = InterfaceFunctions.convertDpToPixel(16, SideMenu.this)
    val paddingTop = InterfaceFunctions.convertDpToPixel(9, SideMenu.this)
    val iconPadding = InterfaceFunctions.convertDpToPixel(5, SideMenu.this)
    val tvHeight = InterfaceFunctions.convertDpToPixel(64, SideMenu.this)
    val tv = new TextView(SideMenu.this)
    tv.setText(item.mTitle)
    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18)
    tv.setPadding(paddingLeft, paddingTop, 0, paddingTop)
    tv.setHeight(tvHeight)
    tv.setGravity(Gravity.CENTER_VERTICAL)
    tv.setCompoundDrawablePadding(iconPadding)
    tv.setTextColor(getResources.getColor(R.color.white))
    tv.setTypeface(FONT.LIGHT.getTypeFace(tv.getContext))
    tv.setCompoundDrawablesWithIntrinsicBounds(item.mIcon, 0, 0, 0)
    tv
  }
}
}

