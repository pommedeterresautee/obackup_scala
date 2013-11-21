package com.pommedeterresautee.twoborange3.Common

import android.app.Activity
import android.support.v4.widget.DrawerLayout

import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.view.GravityCompat
import android.content.Intent
import android.view.{MenuItem, Gravity, ViewGroup, View}
import android.os.Bundle
import android.widget.{TextView, BaseAdapter, AdapterView, ListView}
import android.widget.AdapterView.OnItemClickListener
import android.util.TypedValue
import com.pommedeterresautee.twoborange3.{FONT, InterfaceFunctions, R}

/**
 * Trait to add a side menu to an Activity
 */
trait SideMenu extends Activity {

  case class SideMenuItem(mTitle: Int, mIcon: Int, mIntent: Intent, mShouldFinishCurrentActivity: Boolean = true)

  private var mDrawerLayout: DrawerLayout = _
  private var mDrawerList: ListView = _
  private var mDrawerToggle: ActionBarDrawerToggle = _
  private var mMenuList: List[SideMenuItem] = _

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    //Init side menu content
    mMenuList = List[SideMenuItem](SideMenuItem(R.string.app_name, R.drawable.ic_drawer, null))
    getActionBar.setDisplayHomeAsUpEnabled(true)
    getActionBar.setHomeButtonEnabled(true)
  }

  override def onStart() {
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

  class CustomActionBarDrawerToggle(var activity: Activity, var drawerLayout: DrawerLayout, var drawerImageRes: Int, var openDrawerContentDescRes: Int, var closeDrawerContentDescRes: Int) extends ActionBarDrawerToggle(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes) {

    override def onDrawerClosed(view: View) = invalidateOptionsMenu()

    override def onDrawerOpened(drawerView: View) = invalidateOptionsMenu()
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (mDrawerToggle.onOptionsItemSelected(item)) {
      return true
    }
    super.onOptionsItemSelected(item)
  }

  class DrawerItemClickListener extends OnItemClickListener {
    def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long): Unit = {
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

    override def getCount: Int = mMenuList.size

    override def getItem(position: Int): Object = mMenuList(position)

    override def getItemId(position: Int) = position

    override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
      val item: SideMenuItem = mMenuList(position)
//
//      new SLinearLayout {
//        SButton(item.mTitle).textSize(18 sp).height(64 dip).gravity(Gravity.CENTER_VERTICAL).compoundDrawablePadding(5 dip).textColor(R.color.white).typeface(FONT.LIGHT.getTypeFace(ctx)).<<.wrap.>>
//      }.padding(16 dip, 9 dip, 0 dip, 9 dip)

      val paddingLeft = InterfaceFunctions.Dp2Px(16, SideMenu.this)
      val paddingTop = InterfaceFunctions.Dp2Px(9, SideMenu.this)
      val iconPadding = InterfaceFunctions.Dp2Px(5, SideMenu.this)
      val tvHeight = InterfaceFunctions.Dp2Px(64, SideMenu.this)
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