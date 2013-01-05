#
# A mixin to be 'extend'ed by a model class having the capability of 
# authenticated MOAT REST API resource.
#
module MoatRestAuth
  mattr_accessor :guard, :sys_auth, :last_accessed
  self.guard = Mutex.new
  self.sys_auth = nil
  self.last_accessed = 0
  
  # Returns/Sets the authenticity token
  def auth_token
    now = Time.now
    self.guard.synchronize do
      unless self.sys_auth
        self.last_accessed = now
        self.sys_auth = SysAuth.find
      else
        if now - self.last_accessed > 60 * 25
          begin
            self.sys_auth.destroy
          rescue ActiveResource::UnauthorizedAccess
            # ignorable
          end
          self.sys_auth = SysAuth.find
        end
      end
    end
    self.sys_auth.access_token
  end
end