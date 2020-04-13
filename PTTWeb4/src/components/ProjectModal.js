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
// import ButtonGroup from '@material-ui/core/ButtonGroup';
// import FormControlLabel from '@material-ui/core/FormControlLabel';
// import FormControl from '@material-ui/core/FormControl';
// import FormLabel from '@material-ui/core/FormLabel';
import axios from 'axios';


import { connect } from 'react-redux';
import { actionCreators } from '../models/actions/projectModalActions';

class ProjectModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            projectname: "",
            projectError: false,
        };
        this.url = window.location.href;
        this.userId = this.url.substr(this.url.lastIndexOf('/') + 1);
    }
    handleChange = ({ target }) => {
        this.setState({ [target.name]: target.value });
    };
    onCancel = () => {
        this.setState({ projectname: "" });
        this.props.closeProjectModal();
    };
    editInitialize() {
        if (this.props.editProject) {
            const { selectedProject } = this.props;
            console.log(selectedProject);
            this.setState({ id: selectedProject.id, projectname: selectedProject.projectname });
        }
    }
    postRequest(projectname) {
        axios.post(process.env.REACT_APP_API_URL + `/users/${this.userId}/projects`,
            {
                "projectname": projectname
            })
            .then(resp => {
                console.log(resp);
            })
            .catch(e => console.log(e));
    }
    putRequest(id, projectname) {
        axios.put(process.env.REACT_APP_API_URL + `/users/${this.userId}/projects/${id}`,
            {
                "projectname": projectname
            })
            .then(resp => {
                console.log(resp);
            })
            .catch(e => console.log(e));
    }
    onSubmit = () => {
        if (this.state.projectname !== "") {
            if (this.props.editProject) {
                this.putRequest(this.state.id, this.state.projectname);
                this.props.submitProjectModal({ id: this.state.id, projectname: this.state.projectname });
            } else {
                this.postRequest(this.state.projectname);
                this.props.submitProjectModal({ projectname: this.state.projectname });
            }
            this.setState({ id: 0, projectname: "", projectError: false });
            this.props.closeProjectModal();
        } else {
            let arr = [{ key: "projectname", value: this.state.projectname }];
            for (let field of arr) {
                if (field.value === "") {
                    switch (field.key) {
                        case "projectname":
                            this.setState({ fnError: true });
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (field.key) {
                        case "projectname":
                            this.setState({ fnError: false });
                            break;
                        default:
                            break;
                    }
                }
            }
        }

    };
    render() {
        const { projectModalOpen } = this.props;
        return (
            <div>
                <Dialog
                    open={projectModalOpen}
                    onEnter={() => this.editInitialize()}
                    onClose={this.onCancel}
                    aria-labelledby="form-dialog-title"
                    keepMounted
                >
                    <FormGroup style={{ justifyContent: "center", textAlign: "center" }}>
                        <DialogTitle>Add/Edit Project</DialogTitle>
                        <DialogContent>
                            <DialogContentText> Here is the modal to add or edit projwectss </DialogContentText>
                            <TextField error={this.state.fnError} required name="projectname" value={this.state.projectname} onChange={this.handleChange} variant="outlined" autoFocus margin="dense" label="FirstName" style={{ width: '80%' }} />
                        </DialogContent>
                        <DialogActions style={{ margin: "2%", }}>
                            <Button onClick={this.onCancel} color="primary">Cancel</Button>
                            <Button onClick={this.onSubmit} autoFocus color="secondary">Save</Button>
                        </DialogActions>
                    </FormGroup>
                </Dialog>
            </div>
        );
    }
}
const mapStateToProps = ({ projectModal }) => {
    return projectModal;
};
const mapDispatchToProps = {
    closeProjectModal: actionCreators.closeProjectModal,
    submitProjectModal: actionCreators.submitProjectModal,
    editProjectModal: actionCreators.editProjectModal,
    deleteProjectModal: actionCreators.deleteProjectModal
};

export default connect(mapStateToProps, mapDispatchToProps)(ProjectModal);;
