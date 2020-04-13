const OPEN_USER_MODAL = 'PTTWeb4/modal/OPEN_USER_MODAL';
const CLOSE_USER_MODAL = 'PTTWeb4/modal/CLOSE_USER_MODAL';
const SUBMIT_USER_MODAL = 'PTTWeb4/modal/SUBMIT_USER_MODAL';
const EDIT_USER_MODAL = 'PTTWeb4/modal/EDIT_USER_MODAL';
const DELETE_USER_MODAL = 'PTTWeb4/modal/DELETE_USER_MODAL';

export const actionTypes = {
    OPEN_USER_MODAL,
    CLOSE_USER_MODAL,
    SUBMIT_USER_MODAL,
    EDIT_USER_MODAL,
    DELETE_USER_MODAL
};
const openUserModal = () => ({
    type: OPEN_USER_MODAL
});
const closeUserModal = () => ({
    type: CLOSE_USER_MODAL
});
const submitUserModal = (data) => ({
    userData: data,
    type: SUBMIT_USER_MODAL
});
const editUserModal = (user) => ({
    userId: user.id,
    userFn: user.firstName,
    userLn: user.lastName,
    userEmail: user.email,
    type: EDIT_USER_MODAL
});

const deleteUserModal = (id) => ({
    userId: id,
    type: DELETE_USER_MODAL
});
export const actionCreators = {
    openUserModal,
    closeUserModal,
    submitUserModal,
    editUserModal,
    deleteUserModal
};