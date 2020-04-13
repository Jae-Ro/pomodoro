import React, { Component } from 'react';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
// import Slide from '@material-ui/core/Slide';
// import Fab from '@material-ui/core/Fab';
// import AddIcon from '@material-ui/icons/Add';
// import IconButton from '@material-ui/core/IconButton';
// import CloseIcon from '@material-ui/icons/Close';
// import Checkbox from '@material-ui/core/Checkbox';
import FormGroup from '@material-ui/core/FormGroup';
import axios from 'axios';
// import ButtonGroup from '@material-ui/core/ButtonGroup';
// import FormControlLabel from '@material-ui/core/FormControlLabel';
// import FormControl from '@material-ui/core/FormControl';
// import FormLabel from '@material-ui/core/FormLabel';


import { connect } from 'react-redux';
import { actionCreators } from '../models/actions/userModalActions';

class UserModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            firstName: "",
            lastName: "",
            email: "",
            fnError: false,
            lnError: false,
            emailError: false,
        };
    }
    handleChange = ({ target }) => {
        this.setState({ [target.name]: target.value });
    };
    onCancel = () => {
        this.setState({ firstName: "", lastName: "", email: "" });
        this.props.closeUserModal();
    };
    editInitialize() {
        if (this.props.editUser) {
            const { selectedUser } = this.props;
            this.setState({ id: selectedUser.id, firstName: selectedUser.firstName, lastName: selectedUser.lastName, email: selectedUser.email });
        }
    }
    postRequest(fn, ln, email) {
        axios.post(process.env.REACT_APP_API_URL + "/users/",
            {
                "firstName": fn,
                "lastName": ln,
                "email": email
            })
            .then(resp => {
                console.log(resp);
            })
            .catch(e => console.log(e));
    }
    putRequest(id, fn, ln) {
        axios.put(process.env.REACT_APP_API_URL + `/users/${id}`,
            {
                "firstName": fn,
                "lastName": ln,
            })
            .then(resp => {
                console.log(resp);
            })
            .catch(e => console.log(e));
    }
    onSubmit = () => {
        if (this.state.firstName !== "" && this.state.lastName !== "" && this.state.email !== "") {
            if (this.props.editUser) {
                this.putRequest(this.state.id, this.state.firstName, this.state.lastName);
                this.props.submitUserModal({ firstName: this.state.firstName, lastName: this.state.lastName, email: this.state.email });
            } else {
                this.postRequest(this.state.firstName, this.state.lastName, this.state.email);
                this.props.submitUserModal({ firstName: this.state.firstName, lastName: this.state.lastName, email: this.state.email });
            }
            this.setState({ id: 0, firstName: "", lastName: "", email: "", fnError: false, lnError: false, emailError: false });
            this.props.closeUserModal();
        } else {
            let arr = [{ key: "firstName", value: this.state.firstName }, { key: "lastName", value: this.state.lastName }, { key: "email", value: this.state.email }];
            for (let field of arr) {
                if (field.value === "") {
                    switch (field.key) {
                        case "firstName":
                            this.setState({ fnError: true });
                            break;
                        case "lastName":
                            this.setState({ lnError: true });
                            break;
                        case "email":
                            this.setState({ emailError: true });
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (field.key) {
                        case "firstName":
                            this.setState({ fnError: false });
                            break;
                        case "lastName":
                            this.setState({ lnError: false });
                            break;
                        case "email":
                            this.setState({ emailError: false });
                            break;
                        default:
                            break;
                    }
                }
            }
        }

    };
    render() {
        const { userModalOpen, editUser } = this.props;
        return (
            <div>
                <Dialog
                    open={userModalOpen}
                    onEnter={() => this.editInitialize()}
                    onClose={this.onCancel}
                    aria-labelledby="form-dialog-title"
                    keepMounted
                >
                    <FormGroup style={{ justifyContent: "center", textAlign: "center" }}>
                        <DialogTitle>Add/Edit User</DialogTitle>
                        <DialogContent>
                            <DialogContentText> Here is the modal to add or edit users </DialogContentText>
                            <TextField error={this.state.fnError} required name="firstName" value={this.state.firstName} onChange={this.handleChange} variant="outlined" autoFocus margin="dense" label="FirstName" style={{ width: '80%' }} />
                            <TextField error={this.state.lnError} required name="lastName" value={this.state.lastName} onChange={this.handleChange} variant="outlined" autoFocus margin="dense" label="LastName" style={{ width: '80%' }} />
                            <TextField disabled={editUser} error={this.state.emailError} required name="email" value={this.state.email} onChange={this.handleChange} variant="outlined" autoFocus margin="dense" label="Email" style={{ width: '80%' }} />
                        </DialogContent>
                        <DialogActions style={{ margin: "2%", }}>
                            <Button onClick={this.onCancel} color="secondary">Cancel</Button>
                            <Button onClick={this.onSubmit} autoFocus color="secondary">Save</Button>
                        </DialogActions>
                    </FormGroup>
                </Dialog>
            </div>
        );
    }
}
const mapStateToProps = ({ userModal }) => {
    return userModal;
};
const mapDispatchToProps = {
    closeUserModal: actionCreators.closeUserModal,
    submitUserModal: actionCreators.submitUserModal,
    editUserModal: actionCreators.editUserModal,
    deleteUserModal: actionCreators.deleteUserModal
};

export default connect(mapStateToProps, mapDispatchToProps)(UserModal);
