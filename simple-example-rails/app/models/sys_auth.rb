#
# A special purpose class for authentication of MOAT REST API
#
# /moat/v1/sys/auth
class SysAuth < ActiveResource::Base
  include MoatRest
  self.site = Moat::REST_URI

  # Override because .json extension is not required.
  def self.element_path(id, prefix_options = {}, query_options = nil)
    query_options = {
      a: Moat::APPLICATION_ID,
      u: Moat::API_CLIENT_ID,
      c: Moat::API_CLIENT_SECRET
    } if query_options.nil? || query_options.empty?
    "#{Moat::REST_PATH}/sys/auth#{query_string(query_options)}"
  end
end