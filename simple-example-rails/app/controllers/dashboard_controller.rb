class DashboardController < ApplicationController
  
  def index
    @vibration_devices = VibrationDevice.find(:all, params: { r: "get", f: "image"}) || []
    @shake_events = {}
    @devices = SysDevice.find || []
    @devices.each do |d|
      @shake_events[d.name] = ShakeEvent.find(:all, params: {
        offset: params[:offset], limit: params[:limit]
      }, device: d.uid) || [] # device: is a MoatModel class specific argument.
    end
    @job_list = SysDmjob.find(:all) || []
    @job_histories = RequestHistory.find(:all) || []
  end
  
  def delete_device
    SysDevice.delete(params[:device_uid]) if params[:device_uid]
    redirect_to action: 'index'
  rescue
    flash[:error] = "Cancel the ongoing requests prior to removing the device."
    redirect_to action: 'index'
  end
  
  def vibrate
    VibrationDevice.stub(params[:name]).vibrate_async if params[:name]
    redirect_to action: 'index'
  end
  
  def cancel
    SysDmjob.delete(params[:uid]) if params[:uid]
    redirect_to action: 'index'
  rescue
    flash[:error] = "Nothing to cancel."
    redirect_to action: 'index'
  end
  
  def delete_all_shake_events
    ShakeEvent.delete(params[:uids], {device: params[:device_uid]}) if params[:uids]
    redirect_to action: 'index'
  end
end
