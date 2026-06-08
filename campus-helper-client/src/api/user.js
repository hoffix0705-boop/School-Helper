import request from "@/api/request"

export function getUserList(params) {
  return request.get("/users", { params })
}

export function setUserStatus(id, status) {
  return request.put(`/users/${id}/status`, { status })
}
